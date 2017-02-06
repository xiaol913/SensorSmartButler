package com.xianggao.smartbutler;

import org.joone.engine.FullSynapse;
import org.joone.engine.LinearLayer;
import org.joone.engine.Monitor;
import org.joone.engine.NeuralNetEvent;
import org.joone.engine.NeuralNetListener;
import org.joone.engine.SigmoidLayer;
import org.joone.engine.learning.TeachingSynapse;
import org.joone.io.FileInputSynapse;
import org.joone.io.FileOutputSynapse;
import org.joone.io.MemoryInputSynapse;
import org.joone.io.MemoryOutputSynapse;
import org.joone.net.NeuralNet;

import java.io.File;

/**
 * 项目名：  SmartButler
 * 包名：    com.xianggao.smartbutler
 * 文件名：  XOR_using_NeuralNet
 * 创建者：  Shawn Gao
 * 创建时间：2017/2/5 - 23:51
 * 描述：    JOONE实现
 */

public class XOR_using_NeuralNet implements NeuralNetListener {
    private NeuralNet nnet = null;
    private MemoryInputSynapse inputSynapse, desiredOutputSynapse;
    private FileInputSynapse fileInputSynapse,fileDesiredOutputSynapse;
    private TeachingSynapse trainer;
    LinearLayer input;
    SigmoidLayer hidden, output;
    boolean singleThreadMode = true;
    private File inputFile = new File("F:\\JavaEXP\\Android\\Test\\src\\com\\imooc\\test\\train.txt");
    private File testFile = new File("F:\\JavaEXP\\Android\\Test\\src\\com\\imooc\\test\\test.txt");
    // XOR input
    private double[][] inputArray = new double[][]
            {
                    {0.0, 0.0},
                    {0.0, 1.0},
                    {1.0, 0.0},
                    {1.0, 1.0}};

    // XOR desired output
    private double[][] desiredOutputArray = new double[][]
            {
                    {0.0},
                    {1.0},
                    {1.0},
                    {0.0}};

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        //建立神经网络对象
        XOR_using_NeuralNet xor = new XOR_using_NeuralNet();
        //初始化神经网络
        xor.initNeuralNet();
        //训练
        xor.train();
        //测试方法
        xor.interrogate();
    }

    /**
     * 训练
     * Method declaration
     */
    public void train() {

        /*
        //内存数据
        // set the inputs
        // 初始化输入层数据，也就是指定输入层数据的内容
        inputSynapse.setInputArray(inputArray);
        //输入层数据使用的是inputArray的前两列数据。
        inputSynapse.setAdvancedColumnSelector(" 1,2 ");
        // set the desired outputs
        desiredOutputSynapse.setInputArray(desiredOutputArray);
        desiredOutputSynapse.setAdvancedColumnSelector(" 1 ");
        */

        //文件数据
        fileInputSynapse.setInputFile(inputFile);
        fileInputSynapse.setAdvancedColumnSelector("1,2");//使用文件第1列开始，第2列结束

        fileDesiredOutputSynapse.setInputFile(inputFile);
        fileDesiredOutputSynapse.setAdvancedColumnSelector("3");//第3列结束


        // get the monitor object to train or feed forward
        Monitor monitor = nnet.getMonitor();

        // set the monitor parameters
        // 调节神经网络的参数
        //设置神经网络训练的步长参数，步长越大，神经网络梯度下降的速度越快
        monitor.setLearningRate(0.8);
        monitor.setMomentum(0.3);
        //这个是设置神经网络的输入层的训练数据大小size，这里使用的是数组的长度
        monitor.setTrainingPatterns(4);
        //设置迭代数目
        monitor.setTotCicles(5000);
        //true表示是在训练过程
        monitor.setLearning(true);

        long initms = System.currentTimeMillis();
        // Run the network in single-thread, synchronized mode
        // 是不是使用多线程
        nnet.getMonitor().setSingleThreadMode(singleThreadMode);
        //true = 开始训练
        nnet.go(true);
        System.out.println(" Total time=  "
                + (System.currentTimeMillis() - initms) + "  ms ");
    }

    /**
     * 测试方法
     */
    private void interrogate() {
        // inputArray是测试数据
        double[][] inputArray = new double[][]
                {
                        {1.0, 1.0}};
        /*
        //内存数据
        // set the inputs
        inputSynapse.setInputArray(inputArray);
        inputSynapse.setAdvancedColumnSelector(" 1,2 ");
        */

        //文件数据
        fileInputSynapse.setInputFile(testFile);
        fileInputSynapse.setAdvancedColumnSelector("1,2");

        Monitor monitor = nnet.getMonitor();
        //指测试的数量，4表示有4个测试数据（虽然这里只有一个）
        monitor.setTrainingPatterns(4);
        //设置迭代数目
        monitor.setTotCicles(1);
        //因为这不是训练过程，并不需要学习
        monitor.setLearning(false);
        //初始测试结果，注意到之前我们初始化神经网络的时候并没有给输出层指定数据对象，
        //因为那个时候我们在训练，而且指定了trainer作为目的输出。
        /*
        //内存输出
        MemoryOutputSynapse memOut = new MemoryOutputSynapse();
        */
        FileOutputSynapse memOut = new FileOutputSynapse();

        // set the output synapse to write the output of the net

        if (nnet != null) {
            nnet.addOutputSynapse(memOut);
            System.out.println(nnet.check());
            nnet.getMonitor().setSingleThreadMode(singleThreadMode);
            nnet.go();

//            for (int i = 0; i < 4; i++) {
//                double[] pattern = memOut.getNextPattern();
//                System.out.println(" Output pattern # " + (i + 1) + " = "
//                        + pattern[0]);
//            }
//            System.out.println(" Interrogating Finished ");
        }
    }

    /**
     * 初始化神经网络
     * Method declaration
     */
    protected void initNeuralNet() {

        // 创建三个层面，输入层、隐藏层、输出层
        input = new LinearLayer();
        hidden = new SigmoidLayer();
        output = new SigmoidLayer();


        // 设置每个层面数值
        input.setRows(2);
        hidden.setRows(3);
        output.setRows(1);

        input.setLayerName(" L.input ");
        hidden.setLayerName(" L.hidden ");
        output.setLayerName(" L.output ");

        // 连接这三个层
        FullSynapse synapse_IH = new FullSynapse(); /* input -> hidden conn. */
        FullSynapse synapse_HO = new FullSynapse(); /* hidden -> output conn. */

        // Connect the input layer whit the hidden layer
        input.addOutputSynapse(synapse_IH);
        hidden.addInputSynapse(synapse_IH);

        // Connect the hidden layer whit the output layer
        hidden.addOutputSynapse(synapse_HO);
        output.addInputSynapse(synapse_HO);

        /*
        //从内存中读取数据
        // the input to the neural net
        // 这里指的是使用了从内存中输入数据的方法，指的是输入层输入数据
        inputSynapse = new MemoryInputSynapse();
        input.addInputSynapse(inputSynapse);
        // The Trainer and its desired output
        // 也是从内存中输入数据，指的是从输入层应该输出的数据
        desiredOutputSynapse = new MemoryInputSynapse();
        TeachingSynapse trainer = new TeachingSynapse();
        trainer.setDesired(desiredOutputSynapse);
        */

        //从文件读取数据
        fileInputSynapse = new FileInputSynapse();
        input.addInputSynapse(fileInputSynapse);
        fileDesiredOutputSynapse = new FileInputSynapse();
        TeachingSynapse trainer = new TeachingSynapse();
        trainer.setDesired(fileDesiredOutputSynapse);


        // Now we add this structure to a NeuralNet object
        // 将之前初始化的构件连接成一个神经网络
        nnet = new NeuralNet();

        nnet.addLayer(input, NeuralNet.INPUT_LAYER);
        nnet.addLayer(hidden, NeuralNet.HIDDEN_LAYER);
        nnet.addLayer(output, NeuralNet.OUTPUT_LAYER);
        nnet.setTeacher(trainer);
        output.addOutputSynapse(trainer);
        //这个作用是对神经网络的训练过程进行监听
        nnet.addNeuralNetListener(this);
    }

    /**
     * 每个循环结束后输出的信息
     */
    public void cicleTerminated(NeuralNetEvent e) {
    }

    /**
     * 神经网络错误率变化时候输出的信息
     */
    public void errorChanged(NeuralNetEvent e) {
        Monitor mon = (Monitor) e.getSource();
        if (mon.getCurrentCicle() % 100 == 0)
            System.out.println(" Epoch:  "
                    + (mon.getTotCicles() - mon.getCurrentCicle()) + "  RMSE: "
                    + mon.getGlobalError());
    }

    /**
     * 神经网络开始运行的时候输出的信息
     */
    public void netStarted(NeuralNetEvent e) {
        Monitor mon = (Monitor) e.getSource();
        System.out.print(" Network started for  ");
        if (mon.isLearning())
            System.out.println(" training. ");
        else
            System.out.println(" interrogation. ");
    }

    /**
     * 神经网络停止的时候输出的信息
     */
    public void netStopped(NeuralNetEvent e) {
        Monitor mon = (Monitor) e.getSource();
        System.out.println(" Network stopped. Last RMSE= "
                + mon.getGlobalError());
    }

    public void netStoppedError(NeuralNetEvent e, String error) {
        System.out.println(" Network stopped due the following error:  "
                + error);
    }

}
