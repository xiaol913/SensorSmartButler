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
 * 描述：    Predict
 */

public class ModelHelper {

    private static Evaluator createEvaluator(Context context) throws Exception {
        AssetManager assetManager = null;
        assetManager = context.getAssets();
        InputStream is = assetManager.open("SGDClassifier.ser");
        return EvaluatorUtil.createEvaluator(is);
    }


    public static String predictAction(Context context, double x, double y, double z) throws Exception {
        Evaluator evaluator = createEvaluator(context);
        Map<String, Double> data = new HashMap<>();
        x = Math.abs(x);
        y = Math.abs(y);
        z = Math.abs(z);
        double v = Math.pow(x,2) + Math.pow(y,2) + Math.pow(z,2);
        data.put("AccelerometerX", x);
        data.put("AccelerometerY", y);
        data.put("AccelerometerZ", z);
        data.put("Accelerometer_value",v);
        Map<FieldName, FieldValue> arguments = new LinkedHashMap<>();
        List<InputField> inputFields = evaluator.getInputFields();
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
            }else if (inputFieldName.toString().equals("Accelerometer_value")){
                str =data.get("Accelerometer_value");
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
