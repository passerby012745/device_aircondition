package com.xinlianfeng.android.livehome.devices.wificontrol;

public class WiFiConTrolUtil
{
	private static String hexStr = "0123456789ABCDEF";
	private static byte[] crc8_tab = { (byte) 0, (byte) 94, (byte) 188, (byte) 226,
        (byte) 97, (byte) 63, (byte) 221, (byte) 131, (byte) 194,
        (byte) 156, (byte) 126, (byte) 32, (byte) 163, (byte) 253,
        (byte) 31, (byte) 65, (byte) 157, (byte) 195, (byte) 33,
        (byte) 127, (byte) 252, (byte) 162, (byte) 64, (byte) 30,
        (byte) 95, (byte) 1, (byte) 227, (byte) 189, (byte) 62, (byte) 96,
        (byte) 130, (byte) 220, (byte) 35, (byte) 125, (byte) 159,
        (byte) 193, (byte) 66, (byte) 28, (byte) 254, (byte) 160,
        (byte) 225, (byte) 191, (byte) 93, (byte) 3, (byte) 128,
        (byte) 222, (byte) 60, (byte) 98, (byte) 190, (byte) 224, (byte) 2,
        (byte) 92, (byte) 223, (byte) 129, (byte) 99, (byte) 61,
        (byte) 124, (byte) 34, (byte) 192, (byte) 158, (byte) 29,
        (byte) 67, (byte) 161, (byte) 255, (byte) 70, (byte) 24,
        (byte) 250, (byte) 164, (byte) 39, (byte) 121, (byte) 155,
        (byte) 197, (byte) 132, (byte) 218, (byte) 56, (byte) 102,
        (byte) 229, (byte) 187, (byte) 89, (byte) 7, (byte) 219,
        (byte) 133, (byte) 103, (byte) 57, (byte) 186, (byte) 228,
        (byte) 6, (byte) 88, (byte) 25, (byte) 71, (byte) 165, (byte) 251,
        (byte) 120, (byte) 38, (byte) 196, (byte) 154, (byte) 101,
        (byte) 59, (byte) 217, (byte) 135, (byte) 4, (byte) 90, (byte) 184,
        (byte) 230, (byte) 167, (byte) 249, (byte) 27, (byte) 69,
        (byte) 198, (byte) 152, (byte) 122, (byte) 36, (byte) 248,
        (byte) 166, (byte) 68, (byte) 26, (byte) 153, (byte) 199,
        (byte) 37, (byte) 123, (byte) 58, (byte) 100, (byte) 134,
        (byte) 216, (byte) 91, (byte) 5, (byte) 231, (byte) 185,
        (byte) 140, (byte) 210, (byte) 48, (byte) 110, (byte) 237,
        (byte) 179, (byte) 81, (byte) 15, (byte) 78, (byte) 16, (byte) 242,
        (byte) 172, (byte) 47, (byte) 113, (byte) 147, (byte) 205,
        (byte) 17, (byte) 79, (byte) 173, (byte) 243, (byte) 112,
        (byte) 46, (byte) 204, (byte) 146, (byte) 211, (byte) 141,
        (byte) 111, (byte) 49, (byte) 178, (byte) 236, (byte) 14,
        (byte) 80, (byte) 175, (byte) 241, (byte) 19, (byte) 77,
        (byte) 206, (byte) 144, (byte) 114, (byte) 44, (byte) 109,
        (byte) 51, (byte) 209, (byte) 143, (byte) 12, (byte) 82,
        (byte) 176, (byte) 238, (byte) 50, (byte) 108, (byte) 142,
        (byte) 208, (byte) 83, (byte) 13, (byte) 239, (byte) 177,
        (byte) 240, (byte) 174, (byte) 76, (byte) 18, (byte) 145,
        (byte) 207, (byte) 45, (byte) 115, (byte) 202, (byte) 148,
        (byte) 118, (byte) 40, (byte) 171, (byte) 245, (byte) 23,
        (byte) 73, (byte) 8, (byte) 86, (byte) 180, (byte) 234, (byte) 105,
        (byte) 55, (byte) 213, (byte) 139, (byte) 87, (byte) 9, (byte) 235,
        (byte) 181, (byte) 54, (byte) 104, (byte) 138, (byte) 212,
        (byte) 149, (byte) 203, (byte) 41, (byte) 119, (byte) 244,
        (byte) 170, (byte) 72, (byte) 22, (byte) 233, (byte) 183,
        (byte) 85, (byte) 11, (byte) 136, (byte) 214, (byte) 52,
        (byte) 106, (byte) 43, (byte) 117, (byte) 151, (byte) 201,
        (byte) 74, (byte) 20, (byte) 246, (byte) 168, (byte) 116,
        (byte) 42, (byte) 200, (byte) 150, (byte) 21, (byte) 75,
        (byte) 169, (byte) 247, (byte) 182, (byte) 232, (byte) 10,
        (byte) 84, (byte) 215, (byte) 137, (byte) 107, 53 };
	public static String byteArray2HexString(byte bytes[])
	{
	String result = "";
	String hex = "";
	for (int i = 0; i < bytes.length; i++)
	{
		// 字节高4位
		hex = String.valueOf(hexStr.charAt((bytes[i] & 0xF0) >> 4));
		// 字节低4位
		hex += String.valueOf(hexStr.charAt(bytes[i] & 0x0F));
		result += hex;
	}
	return result;
  }
	/**
     * 计算CRC8校验值
     * 
     * @param data
     *            数据
     * @param offset
     *            起始位置
     * @param len
     *            长度
     * @param preval
     *            之前的校验值
     * @return 校验值
     */
    public static byte calcCrc8(byte[] data, int offset, int len, byte preval) {
            byte ret = preval;
            for (int i = offset; i < (offset + len); ++i) {
                    ret = crc8_tab[(0x00ff & (ret ^ data[i]))];
            }
            return ret;
    }
    /**
     * 计算CRC8校验值
     * 
     * @param data
     *            数据
     * @param offset
     *            起始位置
     * @param len
     *            长度
     * @return 校验值
     */
    public static byte calcCrc8(byte[] data, int offset, int len) {
            return calcCrc8(data, offset, len, (byte) 0);
    }
  public static byte crc8_bytes(byte[] data, int len)
  {
	  return calcCrc8(data, 0, len, (byte) 0);
  }
  public static byte[] shortToByteArray(short s) {  
      byte[] targets = new byte[2];  
      for (int i = 0; i < 2; i++) {  
          int offset = (targets.length - 1 - i) * 8;  
          targets[i] = (byte) ((s >>> offset) & 0xff);  
      }  
      return targets;  
  }
  public static String shortArray2HexString(short shorts[] , int len)
  {
    if ((shorts == null) || (shorts.length == 0)) {
      return null;
    }
    int i = len;
    if (shorts.length < len) {
      i = shorts.length;
    }
    String retstr = new String();
    for(int j=0;j<i;j++){
    	byte [] retbyte=shortToByteArray(shorts[i]);
    	retstr+=byteArray2HexString(retbyte);
    }
    return retstr;
  }
  public static byte stringPerByteSum(byte s[], int len){
	  int i=0;
	  byte sum=0;
	  for(i=0;i< len;i++){
		  sum+=i*s[i];
	  }
	  return (byte)(sum&0x7f);
  }
  /** 通用 字符串格式化成JSON格式 */
	public static String formatStringToJSON(Object... objects) {
		StringBuilder jsonBuilder = new StringBuilder();
		jsonBuilder.append("{");
		for (int i = 0; i < objects.length - 1; i += 2) {
			if (objects[i + 1] instanceof String) {
				objects[i + 1] = "\"" + objects[i + 1] + "\"";
			}
			jsonBuilder.append("\"" + objects[i] + "\"" + ":" + objects[i + 1]);
			jsonBuilder.append(",");
		}
		jsonBuilder = jsonBuilder.delete(jsonBuilder.length() - 1, jsonBuilder.length());
		jsonBuilder.append("}");
		//LogUtils.d(TAG_JSON, "jsonBuilder.toString() : " + jsonBuilder.toString());
		return jsonBuilder.toString();
	}
	
	public static int byteArrayToInt(byte[] b, int index){
		int value= 0;
		for (int i = 0; i < 4; i++) {
			value |= b[index+i];
			if(i < 3)
				value <<= 8;
		}
		return value;
	}
}