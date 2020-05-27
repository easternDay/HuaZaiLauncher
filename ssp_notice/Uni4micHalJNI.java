package com.unisound.jni;

public class Uni4micHalJNI {
  /**
   * 设置4mic唤醒or 识别状态
   *
   * @param status 0：唤醒状态 , 1：识别状态 ，default:0
   */
  public native int set4MicWakeUpStatus(int status);

  /**
   * 设置4mic utteranceTime , 唤醒词时长
   *
   * @param length 时间长度 ms
   */
  public native int set4MicUtteranceTimeLen(int length);

  /**
   * 设置4mic delayTime 从唤醒词结束到引擎返回结果
   *
   * @param delayTime 时间长度 ms
   */
  public native int set4MicDelayTime(int delayTime);

  /**
   * 关闭4mic算法 如果关闭4mic算法，录音数据不经过4mic算法直接输出
   *
   * @param status 0:打开4mic算法 ， 1：关闭4mic算法 ， default:0
   * @return 执行结果
   */
  public native int close4MicAlgorithm(int flag);

  /**
   * 标记本次read录音的起始点，value=delayTime 识别相对于录音的延时
   *
   * @param startTimeLen 时间长度 ms
   */
  public native int set4MicOneShotStartLen(int startLength);

  /**
   * 从标记点开始计算，到唤醒起始点的时长（ms）
   */
  public native int set4MicWakeupStartLen(int startLength);

  /**
   * 设置是否准备好开始oneshot
   *
   * @param status 0:没准备好 ， 1:已经准备好
   */
  public native int set4MicOneShotReady(int flag);

  /**
   * 返回4mic Oneshot 是否准备好
   *
   * @return 0:没有准备好 ， 1：已准备好
   */
  public native int get4MicOneShotReady();

  /**
   * 获取4micDoa结果
   *
   * @return 返回Doa result int 信息 0-360
   */
  public native int get4MicDoaResult();

  /**
   * 返回4mic版本信息
   *
   * @return 版本信息
   */
  public native String get4MicBoardVersion();

  /**
   * 设置4mic debug模式
   *
   * @param debugMode 0：关闭debug ， 1：打开debug ， default:0
   */
  public native int set4MicDebugMode(int mode);

  // audio interface
  // //////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * 打开录音设备
   *
   * @param chNum android中固定使用2 e.g.: openAudioIn(1,2)
   * @return handle
   */
  public native long openAudioIn(int chNum);

  /**
   * 关闭录音设备
   *
   * @param handle open是产生的handle
   * @return -1：失败 ， 0：成功
   */
  public native int closeAudioIn(long handle);

  /**
   * 读取 size 大小的声音到buffer里。
   *
   * @param handle open是产生的handle
   * @return 实际读取的字节数
   */
  public native int readData(long handle, byte[] buffer, int bufferLength);

  /**
   * 开始录音
   *
   * @param handle open时产生的handle
   * @return -1：失败 ， 0：成功
   */
  public native int startRecorder(long handle);

  /**
   * 停止录音
   *
   * @param handle open时产生的handle
   * @return -1：失败 ， 0：成功
   */
  public native int stopRecorder(long handle);


  /**
   * 设置4mic角度抑制
   *
   * @param handle 0为正对面 否则为doa角度方向
   * @return -1：失败 ， 0：成功
   */
  public native int set4MicSSLON(int status);


  /**
   * 二次唤醒增强
   *
   * @param
   * @return
   */
  public native int set4MicDualWakeup(int mode);


  /**
   * 获取正在录音的数量
   *
   * @param
   * @return
   */
  public native int get4MicRecordercount();

  /**
   * @param use4Mic 0:not use 4mic; 1:use 4mic
   */
  public native int initHal(int use4Mic, String key);

  /**
   * 获取Jni 库版本号
   */
  public native String get4MicJniVersion();

  /**
   * @return 释放4mic的资源
   */
  public native int releaseHal();

  static {
    System.loadLibrary("Uni4micHalJNI");
  }
}
