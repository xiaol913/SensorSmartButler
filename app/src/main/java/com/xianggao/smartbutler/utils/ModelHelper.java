package com.xianggao.smartbutler.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.xianggao.smartbutler.entity.CountData;

import org.dmg.pmml.FieldName;
import org.jpmml.android.EvaluatorUtil;
import org.jpmml.evaluator.Computable;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.InputField;
import org.jpmml.evaluator.TargetField;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * 项目名：  SmartButler
 * 包名：    com.xianggao.smartbutler.utils
 * 文件名：  ModelHelper
 * 创建者：  Shawn Gao
 * 创建时间：2017/3/11 - 3:13
 * 描述：    Predict Action
 */

public class ModelHelper {

    private Evaluator evaluator;

    private static Map<String, Double> analysisData(Map<String, Double> data, double[] list, String type) {
        double x = 0;
        double y = 0;
        double z = 0;
        if (type.equals("Accelerometer")) {
            x = list[0];
            y = list[1];
            z = list[2];
        } else if (type.equals("Gravity")) {
            x = list[3];
            y = list[4];
            z = list[5];
        } else if (type.equals("Linear")) {
            x = list[6];
            y = list[7];
            z = list[8];
        }
        String strX = type + "X";
        String strY = type + "Y";
        String strZ = type + "Z";
        data.put(strX, x);
        data.put(strY, y);
        data.put(strZ, z);
        return data;
    }

    private static double[] normalize(double[] values) {
        double[] mean = {
                0.172987568246, -0.59553202046, 4.46550770779,
                -0.149157725964, -0.62031950318, 4.43278505239,
                0.32214529421, 0.0247874827203, 0.0327226553929
        };
        double[] std = {
                2.465133, 3.433202, 7.775168,
                2.830752, 3.278206, 7.572936,
                1.884442, 1.235964, 1.079798
        };
        double[] result = new double[9];
        for (int i = 0; i < values.length; i++) {
            result[i] = (values[i] - mean[i]) / std[i];
        }
        return result;
    }

    public ModelHelper(Context context) {
        AssetManager assetManager = context.getAssets();
        InputStream is = null;
        try {
            is = assetManager.open("MLPClassifier_new.ser");
            this.evaluator = EvaluatorUtil.createEvaluator(is);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String predictAction(double[] array, CountData countData) {
        double[] list = normalize(array);
        Map<String, Double> data = new HashMap<>();
        analysisData(data, list, "Accelerometer");
        analysisData(data, list, "Gravity");
        analysisData(data, list, "Linear");
        Map<FieldName, FieldValue> arguments = new LinkedHashMap<>();
        List<InputField> inputFields = this.evaluator.getInputFields();
        for (InputField inputField : inputFields) {
            FieldName inputFieldName = inputField.getName();
            FieldValue inputFieldValue = null;
            Object str = null;
            if (inputFieldName.toString().equals("AccelerometerX")) {
                str = data.get("AccelerometerX");
            } else if (inputFieldName.toString().equals("AccelerometerY")) {
                str = data.get("AccelerometerY");
            } else if (inputFieldName.toString().equals("AccelerometerZ")) {
                str = data.get("AccelerometerZ");
            } else if (inputFieldName.toString().equals("GravityX")) {
                str = data.get("GravityX");
            } else if (inputFieldName.toString().equals("GravityY")) {
                str = data.get("GravityY");
            } else if (inputFieldName.toString().equals("GravityZ")) {
                str = data.get("GravityZ");
            } else if (inputFieldName.toString().equals("LinearX")) {
                str = data.get("LinearX");
            } else if (inputFieldName.toString().equals("LinearY")) {
                str = data.get("LinearY");
            } else if (inputFieldName.toString().equals("LinearZ")) {
                str = data.get("LinearZ");
            }
            try {
                inputFieldValue = inputField.prepare(str);
            } catch (Exception e) {
            }
            arguments.put(inputFieldName, inputFieldValue);
        }
        Map<FieldName, ?> results = evaluator.evaluate(arguments);
        List<TargetField> targetFields = evaluator.getTargetFields();
        Object targetFieldValue = null;
        for (TargetField targetField : targetFields) {
            FieldName targetFieldName = targetField.getName();
            targetFieldValue = results.get(targetFieldName);
        }
        Object unBoxedTargetFieldValue = null;
        if (targetFieldValue instanceof Computable) {
            Computable computable = (Computable) targetFieldValue;

            unBoxedTargetFieldValue = computable.getResult();
        }
        switch (unBoxedTargetFieldValue.toString()) {
            case "2":
                countData.setCountV(countData.getCountV() + 1);
                break;
            case "1":
                countData.setCountS(countData.getCountS() + 1);
                break;
            case "0":
                countData.setCountF(countData.getCountF() + 1);
                break;
        }
        String resultCode = null;
        if (countData.getCountF() >= countData.getCountS() &&
                countData.getCountF() >= countData.getCountS()) {
            resultCode = "OnFeet";
        } else if (countData.getCountS() >= countData.getCountF() &&
                countData.getCountS() >= countData.getCountV()) {
            resultCode = "Still";
        } else if (countData.getCountV() >= countData.getCountF() &&
                countData.getCountV() >= countData.getCountS()) {
            resultCode = "InVehicle";
        }
        return resultCode;
    }
}
