package com.xianggao.smartbutler.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.xianggao.smartbutler.entity.SQLData;

import org.dmg.pmml.FieldName;
import org.jpmml.android.EvaluatorUtil;
import org.jpmml.evaluator.Computable;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.InputField;
import org.jpmml.evaluator.TargetField;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

    private static Map<String, Double> analysisData(Map<String, Double> data, double[] list) {
        data.put("AX_max", list[0]);
        data.put("AX_min", list[1]);
        data.put("AY_max", list[2]);
        data.put("AY_min", list[3]);
        data.put("AZ_max", list[4]);
        data.put("AZ_min", list[5]);

        data.put("GX_max", list[6]);
        data.put("GX_min", list[7]);
        data.put("GY_max", list[8]);
        data.put("GY_min", list[9]);
        data.put("GZ_max", list[10]);
        data.put("GZ_min", list[11]);

        data.put("LX_max", list[12]);
        data.put("LX_min", list[13]);
        data.put("LY_max", list[14]);
        data.put("LY_min", list[15]);
        data.put("LZ_max", list[16]);
        data.put("LZ_min", list[17]);
        return data;
    }

    private static double[] normalize(double[][] values) {
        double[] mean = new double[9];
        double[] std = new double[9];
        double[][] result = new double[values.length][9];
        double[] res_data = new double[18];

        for (int column = 0; column < 9; column++) {
            double sum = 0;
            for (int row = 0; row < values.length; row++) {
                sum = sum + values[row][column];
            }
            mean[column] = sum / values.length;
            double sum_1 = 0;
            for (int row_1 = 0; row_1 < values.length; row_1++) {
                sum_1 = sum_1 + (values[row_1][column] - mean[column]) * (values[row_1][column] - mean[column]);
            }
            std[column] = Math.sqrt((sum_1 / values.length));
        }

        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < 9; j++) {
                result[i][j] = (values[i][j] - mean[j]) / std[j];
            }
        }

        int index = 0;
        for (int i = 0; i < 9; i++) {
            double max = result[i][0];
            double min = result[i][0];
            for (int j = 0; j < values.length; j++) {
                if (values[j][i] > max) {
                    max = values[j][i];
                }
                if (values[j][i] < min) {
                    min = values[j][i];
                }
            }
            res_data[index] = max;
            index++;
            res_data[index] = min;
            index++;
        }
        return res_data;
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

    public String predictAction(ArrayList<SQLData> dataList) {
        double[][] data_list = recordData(dataList);
        double[] list = normalize(data_list);
        Map<String, Double> data = new HashMap<>();
        analysisData(data, list);
        Map<FieldName, FieldValue> arguments = new LinkedHashMap<>();
        List<InputField> inputFields = this.evaluator.getInputFields();
        for (InputField inputField : inputFields) {
            FieldName inputFieldName = inputField.getName();
            FieldValue inputFieldValue = null;
            Object str = null;
            if (inputFieldName.toString().equals("AX_max")) {
                str = data.get("AX_max");
            } else if (inputFieldName.toString().equals("AX_min")) {
                str = data.get("AX_min");
            } else if (inputFieldName.toString().equals("AY_max")) {
                str = data.get("AY_max");
            } else if (inputFieldName.toString().equals("AY_min")) {
                str = data.get("AY_min");
            } else if (inputFieldName.toString().equals("AZ_max")) {
                str = data.get("AZ_max");
            } else if (inputFieldName.toString().equals("AZ_min")) {
                str = data.get("AZ_min");
            } else if (inputFieldName.toString().equals("GX_max")) {
                str = data.get("GX_max");
            } else if (inputFieldName.toString().equals("GX_min")) {
                str = data.get("GX_min");
            } else if (inputFieldName.toString().equals("GY_max")) {
                str = data.get("GY_max");
            } else if (inputFieldName.toString().equals("GY_min")) {
                str = data.get("GY_min");
            } else if (inputFieldName.toString().equals("GZ_max")) {
                str = data.get("GZ_max");
            } else if (inputFieldName.toString().equals("GZ_min")) {
                str = data.get("GZ_min");
            } else if (inputFieldName.toString().equals("LX_max")) {
                str = data.get("LX_max");
            } else if (inputFieldName.toString().equals("LX_min")) {
                str = data.get("LX_min");
            } else if (inputFieldName.toString().equals("LY_max")) {
                str = data.get("LY_max");
            } else if (inputFieldName.toString().equals("LY_min")) {
                str = data.get("LY_min");
            } else if (inputFieldName.toString().equals("LZ_max")) {
                str = data.get("LZ_max");
            } else if (inputFieldName.toString().equals("LZ_min")) {
                str = data.get("LZ_min");
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
        return unBoxedTargetFieldValue.toString();
    }

    private double[][] recordData(ArrayList<SQLData> dataList) {
        double[][] result = new double[dataList.size()][9];
        for (int i = 0; i < dataList.size(); i++) {
            result[i][0] = dataList.get(i).getX();
            result[i][1] = dataList.get(i).getY();
            result[i][2] = dataList.get(i).getZ();
            result[i][3] = dataList.get(i).getA();
            result[i][4] = dataList.get(i).getS();
            result[i][5] = dataList.get(i).getD();
            result[i][6] = dataList.get(i).getQ();
            result[i][7] = dataList.get(i).getW();
            result[i][8] = dataList.get(i).getE();
        }
        return result;
    }
}
