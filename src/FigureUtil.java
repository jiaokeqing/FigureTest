import com.zkteco.biometric.FingerprintSensorErrorCode;
import com.zkteco.biometric.FingerprintSensorEx;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 指纹仪工具类
 *
 * @author jkq
 * @description 设备型号：ZKFinger Live20R
 * @createDate 2021.6.17
 */
public class FigureUtil {
    /**
     * 图像宽度
     */
    int fpWidth = 0;
    /**
     * 图像高度
     */
    int fpHeight = 0;
    /**
     * 用于验证
     */
    private byte[] lastRegTemp = new byte[2048];
    /**
     * 最后一次注册模板的长度
     */
    private int cbRegTemp = 0;
    /**
     * 再一次注册的模板
     */
    private byte[][] regtemparray = new byte[3][2048];
    /**
     * 注册
     */
    private boolean bRegister = false;
    /**
     * 识别
     */
    private boolean bIdentify = true;
    /**
     * 指纹id
     */
    private int iFid = 1;

    /**
     * 防假开关 0关闭 1开启
     */
    private int nFakeFunOn = 1;
    /**
     * 录入次数 必须是3
     */
    static final int enroll_cnt = 3;
    /**
     * 预寄存器函数索引
     */
    private int enroll_idx = 0;

    private byte[] imgbuf = null;
    private byte[] template = new byte[2048];
    private int[] templateLen = new int[1];


    private boolean mbStop = true;
    /**
     * 设备句柄
     */
    private long mhDevice = 0;
    /**
     * 算法句柄
     */
    private long mhDB = 0;
    private WorkThread workThread = null;

    /**
     * 开启指纹仪设备
     */
    public void open() {
        if (0 != mhDevice) {
            //已经被初始化过
            System.out.println("请先关闭设备");
            return;
        }
        int ret = FingerprintSensorErrorCode.ZKFP_ERR_OK;
        //初始化
        cbRegTemp = 0;
        bRegister = false;
        bIdentify = false;
        iFid = 1;
        enroll_idx = 0;
        if (FingerprintSensorErrorCode.ZKFP_ERR_OK != FingerprintSensorEx.Init()) {
            System.out.println("设备初始化失败");
            return;
        }
        ret = FingerprintSensorEx.GetDeviceCount();
        if (ret < 0) {
            System.out.println("设备未连接");
            freeSensor();
            return;
        }
        if (0 == (mhDevice = FingerprintSensorEx.OpenDevice(0))) {
            System.out.println("设备开启失败，错误代码：" + ret);
            freeSensor();
            return;
        }
        if (0 == (mhDB = FingerprintSensorEx.DBInit())) {
            System.out.println("初始化算法库失败，错误代码：" + ret);
            freeSensor();
            return;
        }
        byte[] paramValue = new byte[4];
        int[] size = new int[1];
        size[0] = 4;
        //获取图形宽度
        FingerprintSensorEx.GetParameters(mhDevice, 1, paramValue, size);
        fpWidth = byteArrayToInt(paramValue);
        size[0] = 4;
        //获取图像长度
        FingerprintSensorEx.GetParameters(mhDevice, 2, paramValue, size);
        fpHeight = byteArrayToInt(paramValue);
        //初始化图像数组
        imgbuf = new byte[fpWidth * fpHeight];

        mbStop = false;

        workThread = new WorkThread();
        // 启动监测指纹录入线程
        workThread.start();
        System.out.println("设备启动成功");
        System.out.println("算法句柄：" + mhDB);
        System.out.println("设备句柄：" + mhDevice);
    }

    /**
     * 关闭指纹仪设备
     */
    public void close() {
        freeSensor();
        System.out.println("设备已关闭");
    }

    /**
     * 初始化指纹算法库
     */
    public void init() {
        //TODO 将数据库中的指纹添加到算法库
    }

    /**
     * 指纹录入
     */
    public void enroll() {
        if (0 == mhDevice) {
            System.out.println("请先开启设备");
            return;
        }
        if (!bRegister) {
            enroll_idx = 0;
            bRegister = true;
            System.out.println("请连续输入三次指纹");
        }
    }

    /**
     * 指纹验证
     */
    public void verify() {
        if (0 == mhDevice) {
            System.out.println("请先开启设备");
            return;
        }
        if (bRegister) {
            enroll_idx = 0;
            bRegister = false;
        }
        if (bIdentify) {
            bIdentify = false;
        }
    }

    /**
     * 指纹身份识别
     */
    public void identify() {
        if (0 == mhDB) {
            System.out.println("请先开启设备");
        }
        //TODO 更改指纹图像存放位置
        String path = "d:\\test\\fingerprint.bmp";
        byte[] fpTemplate = new byte[2048];
        int[] sizeFPTemp = new int[1];
        sizeFPTemp[0] = 2048;
        int ret = FingerprintSensorEx.ExtractFromImage(mhDB, path, 500, fpTemplate, sizeFPTemp);
        if (0 == ret) {
            if (bIdentify) {
                int[] fid = new int[1];
                int[] score = new int[1];
                ret = FingerprintSensorEx.DBIdentify(mhDB, fpTemplate, fid, score);
                if (ret == 0) {
                    System.out.println("身份认证成功，指纹id：" + fid[0] + ",对比分数：" + score[0]);
                } else {
                    System.out.println("身份认证失败，错误代码：" + ret);
                }

            } else {
                if (cbRegTemp <= 0) {
                    System.out.println("请先注册");
                } else {
                    ret = FingerprintSensorEx.DBMatch(mhDB, lastRegTemp, fpTemplate);
                    if (ret > 0) {
                        System.out.println("验证成功,对比分数：" + ret);
                    } else {
                        System.out.println("验证失败，错误代码：" + ret);
                    }
                }
            }
        } else {
            System.out.println("从图像中提取指纹失败，错误代码：" + ret);
        }

    }


    /**
     * @description 释放设备和算法
     */
    private void freeSensor() {
        mbStop = true;
        try {
            //等待线程结束
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //判断算法库是否初始化过
        if (0 != mhDB) {
            FingerprintSensorEx.DBFree(mhDB);
            mhDB = 0;
        }
        //判断设备是否打开
        if (0 != mhDevice) {
            FingerprintSensorEx.CloseDevice(mhDevice);
            mhDevice = 0;
        }
        //关闭设备
        FingerprintSensorEx.Terminate();
    }


    private void writeBitmap(byte[] imageBuf, int nWidth, int nHeight,
                             String path) throws IOException {
        FileOutputStream fos = new FileOutputStream(path);
        DataOutputStream dos = new DataOutputStream(fos);

        int w = (((nWidth + 3) / 4) * 4);
        // 位图文件类型（0—1字节）
        int bfType = 0x424d;
        // bmp文件的大小（2—5字节）
        int bfSize = 54 + 1024 + w * nHeight;
        // 位图文件保留字，必须为0（6-7字节）
        int bfReserved1 = 0;
        // 位图文件保留字，必须为0（8-9字节）
        int bfReserved2 = 0;
        // 文件头开始到位图实际数据之间的字节的偏移量（10-13字节）
        int bfOffBits = 54 + 1024;
        // 输入位图文件类型'BM'
        dos.writeShort(bfType);
        // 输入位图文件大小
        dos.write(changeByte(bfSize), 0, 4);
        // 输入位图文件保留字
        dos.write(changeByte(bfReserved1), 0, 2);
        // 输入位图文件保留字
        dos.write(changeByte(bfReserved2), 0, 2);
        // 输入位图文件偏移量
        dos.write(changeByte(bfOffBits), 0, 4);
        // 信息头所需的字节数（14-17字节）
        int biSize = 40;
        // 位图的宽（18-21字节）
        int biWidth = nWidth;
        // 位图的高（22-25字节）
        int biHeight = nHeight;
        // 目标设备的级别，必须是1（26-27字节）
        int biPlanes = 1;
        // 每个像素所需的位数（28-29字节），必须是1位（双色）、4位（16色）、8位（256色）或者24位（真彩色）之一。
        int biBitcount = 8;
        // 位图压缩类型，必须是0（不压缩）（30-33字节）、1（BI_RLEB压缩类型）或2（BI_RLE4压缩类型）之一。
        int biCompression = 0;
        // 实际位图图像的大小，即整个实际绘制的图像大小（34-37字节）
        int biSizeImage = w * nHeight;
        // 位图水平分辨率，每米像素数（38-41字节）这个数是系统默认值
        int biXPelsPerMeter = 0;
        // 位图垂直分辨率，每米像素数（42-45字节）这个数是系统默认值
        int biYPelsPerMeter = 0;
        // 位图实际使用的颜色表中的颜色数（46-49字节），如果为0的话，说明全部使用了
        int biClrUsed = 0;
        // 位图显示过程中重要的颜色数(50-53字节)，如果为0的话，说明全部重要
        int biClrImportant = 0;
        // 输入信息头数据的总字节数
        dos.write(changeByte(biSize), 0, 4);
        // 输入位图的宽
        dos.write(changeByte(biWidth), 0, 4);
        // 输入位图的高
        dos.write(changeByte(biHeight), 0, 4);
        // 输入位图的目标设备级别
        dos.write(changeByte(biPlanes), 0, 2);
        // 输入每个像素占据的字节数
        dos.write(changeByte(biBitcount), 0, 2);
        // 输入位图的压缩类型
        dos.write(changeByte(biCompression), 0, 4);
        // 输入位图的实际大小
        dos.write(changeByte(biSizeImage), 0, 4);
        // 输入位图的水平分辨率
        dos.write(changeByte(biXPelsPerMeter), 0, 4);
        // 输入位图的垂直分辨率
        dos.write(changeByte(biYPelsPerMeter), 0, 4);
        // 输入位图使用的总颜色数
        dos.write(changeByte(biClrUsed), 0, 4);
        // 输入位图使用过程中重要的颜色数
        dos.write(changeByte(biClrImportant), 0, 4);

        for (int i = 0; i < 256; i++) {
            dos.writeByte(i);
            dos.writeByte(i);
            dos.writeByte(i);
            dos.writeByte(0);
        }

        byte[] filter = null;
        if (w > nWidth) {
            filter = new byte[w - nWidth];
        }

        for (int i = 0; i < nHeight; i++) {
            dos.write(imageBuf, (nHeight - 1 - i) * nWidth, nWidth);
            if (w > nWidth) {
                dos.write(filter, 0, w - nWidth);
            }
        }
        dos.flush();
        dos.close();
        fos.close();
    }

    private byte[] changeByte(int data) {
        return intToByteArray(data);
    }

    private byte[] intToByteArray(final int number) {
        byte[] abyte = new byte[4];
        abyte[0] = (byte) (0xff & number);
        abyte[1] = (byte) ((0xff00 & number) >> 8);
        abyte[2] = (byte) ((0xff0000 & number) >> 16);
        abyte[3] = (byte) ((0xff000000 & number) >> 24);
        return abyte;
    }

    private int byteArrayToInt(byte[] bytes) {
        int number = bytes[0] & 0xFF;
        number |= ((bytes[1] << 8) & 0xFF00);
        number |= ((bytes[2] << 16) & 0xFF0000);
        number |= ((bytes[3] << 24) & 0xFF000000);
        return number;
    }

    private void onCatpureOK(byte[] imgBuf) {
        try {
            writeBitmap(imgBuf, fpWidth, fpHeight, "fingerprint.bmp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onExtractOK(byte[] template, int len) {
        if (bRegister) {
            int[] fid = new int[1];
            int[] score = new int[1];
            int ret = FingerprintSensorEx.DBIdentify(mhDB, template, fid, score);
            //判断指纹是否被录入过
            if (ret == 0) {
                System.out.println("此指纹已被指纹" + fid[0] + "录入");
                bRegister = false;
                enroll_idx = 0;
                return;
            }
            //判断3次指纹是否一致
            if (enroll_idx > 0 && FingerprintSensorEx.DBMatch(mhDB, regtemparray[enroll_idx - 1], template) <= 0) {
                System.out.println("录入时请用同一根手指按3次");
                return;
            }

            System.arraycopy(template, 0, regtemparray[enroll_idx], 0, 2048);
            enroll_idx++;
            //判断是否是第三次输入指纹
            if (enroll_idx == 3) {
                int[] _retLen = new int[1];
                _retLen[0] = 2048;
                byte[] regTemp = new byte[_retLen[0]];

                //判断三次指纹是否一致
                if (0 == (ret = FingerprintSensorEx.DBMerge(mhDB, regtemparray[0], regtemparray[1], regtemparray[2], regTemp, _retLen)) &&
                        0 == (ret = FingerprintSensorEx.DBAdd(mhDB, iFid, regTemp))) {
                    iFid++;
                    cbRegTemp = _retLen[0];
                    System.arraycopy(regTemp, 0, lastRegTemp, 0, cbRegTemp);
                    String strBase64 = FingerprintSensorEx.BlobToBase64(regTemp, cbRegTemp);
                    //TODO 将指纹存储到数据库
                    System.out.println("指纹录入成功");
                } else {
                    System.out.println("指纹录入失败，错误代码：" + ret);
                }
                bRegister = false;
            } else {
                System.out.println("还需再输入" + (3 - enroll_idx) + "次指纹");
            }
        } else {
            if (bIdentify) {
                int[] fid = new int[1];
                int[] score = new int[1];
                int ret = FingerprintSensorEx.DBIdentify(mhDB, template, fid, score);
                if (ret == 0) {
                    System.out.println("身份认证成功，指纹id：" + fid[0] + ",对比分数：" + score[0]);
                } else {
                    System.out.println("身份认证失败，错误代码：" + ret);
                }

            } else {
                if (cbRegTemp <= 0) {
                    System.out.println("请先注册");
                } else {
                    int ret = FingerprintSensorEx.DBMatch(mhDB, lastRegTemp, template);
                    if (ret > 0) {
                        System.out.println("验证成功，对比分数：" + ret);
                    } else {
                        System.out.println("验证失败，错误代码：" + ret);
                    }
                }
            }
        }
    }

    private class WorkThread extends Thread {
        @Override
        public void run() {
            super.run();
            int ret = 0;
            //只要未关闭线程就一直运行
            while (!mbStop) {
                templateLen[0] = 2048;
                if (0 == (ret = FingerprintSensorEx.AcquireFingerprint(mhDevice, imgbuf, template, templateLen))) {
                    if (nFakeFunOn == 1) {
                        byte[] paramValue = new byte[4];
                        int[] size = new int[1];
                        size[0] = 4;
                        int nFakeStatus = 0;
                        //code=2004 判断是否为真手指
                        ret = FingerprintSensorEx.GetParameters(mhDevice, 2004, paramValue, size);
                        nFakeStatus = byteArrayToInt(paramValue);
                        System.out.println("ret = " + ret + ",nFakeStatus=" + nFakeStatus);
                        if (0 == ret && (byte) (nFakeStatus & 31) != 31) {
                            return;
                        }
                    }
                    //生成指纹图像
                    onCatpureOK(imgbuf);
                    onExtractOK(template, templateLen[0]);
                    String strBase64 = FingerprintSensorEx.BlobToBase64(template, templateLen[0]);
                    System.out.println("strBase64=" + strBase64);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }


    }
}


