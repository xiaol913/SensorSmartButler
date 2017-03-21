package com.xianggao.smartbutler.utils;

import android.content.Context;
import android.content.res.AssetManager;

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

    private static Map<String, Double> analysisData(Map<String, Double> data, double x, double y, double z, String type) {
        double a = Math.abs(x);
        double b = Math.abs(y);
        double c = Math.abs(z);
        double v = Math.pow(a, 2) + Math.pow(b, 2) + Math.pow(c, 2);
        String strX = type + "X";
        String strY = type + "Y";
        String strZ = type + "Z";
        String strV = type + "_value";
        data.put(strX, a);
        data.put(strY, b);
        data.put(strZ, c);
        data.put(strV, v);
        return data;
    }

    public ModelHelper(Context context){
        AssetManager assetManager = context.getAssets();
        InputStream is = null;
        try {
            is = assetManager.open("MLPClassifier.ser");
            this.evaluator = EvaluatorUtil.createEvaluator(is);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String predictAction(double[] list){
        Map<String, Double> data = new HashMap<>();
        analysisData(data, list[0], list[1], list[2], "Accelerometer");
        analysisData(data, list[3], list[4], list[5], "Gravity");
        analysisData(data, list[6], list[7], list[8], "Linear");
        Map<FieldName, FieldValue> arguments = new LinkedHashMap<>();
        List<InputField> inputFields = this.evaluator.getInputFields();
        for (InputField inputField : inputFields) {
            FieldName inputFieldName = inputField.getName();
            FieldValue inputFieldValue;
            Object str = null;
            if (inputFieldName.toString().equals("AccelerometerX")) {
                str = data.get("AccelerometerX");
            } else if (inputFieldName.toString().equals("AccelerometerY")) {
                str = data.get("AccelerometerY");
            } else if (inputFieldName.toString().equals("AccelerometerZ")) {
                str = data.get("AccelerometerZ");
            } else if (inputFieldName.toString().equals("Accelerometer_value")) {
                str = data.get("Accelerometer_value");
            } else if (inputFieldName.toString().equals("GravityX")) {
                str = data.get("GravityX");
            } else if (inputFieldName.toString().equals("GravityY")) {
                str = data.get("GravityY");
            } else if (inputFieldName.toString().equals("GravityZ")) {
                str = data.get("GravityZ");
            } else if (inputFieldName.toString().equals("Gravity_value")) {
                str = data.get("Gravity_value");
            } else if (inputFieldName.toString().equals("LinearX")) {
                str = data.get("LinearX");
            } else if (inputFieldName.toString().equals("LinearY")) {
                str = data.get("LinearY");
            } else if (inputFieldName.toString().equals("LinearZ")) {
                str = data.get("LinearZ");
            } else if (inputFieldName.toString().equals("Linear_value")) {
                str = data.get("Linear_value");
            }
            inputFieldValue = inputField.prepare(str);
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
        return unBoxedTargetFieldValue.toString();
    }
}
