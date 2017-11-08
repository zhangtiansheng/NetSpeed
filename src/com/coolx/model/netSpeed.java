/**   
* @Title: netSpeed.java
* @Package hq.memFloat.model
* @Description: TODO
* @date 2013-7-26 上午10:55:00
* @version V1.0   
*/
package com.coolx.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.util.Log;

/**
 * @ClassName: netSpeed
 * @Description: TODO
 * @date 2013-7-26 上午10:55:00
 * 
 */
public class netSpeed {
	public static long[] getSpeed(String dev) {
		long[] mTotal = new long[2] ;
		
		String path = "/proc/net/dev";
		
		String content = null;
		String receive = null;
		String transmit = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path));
			String line;
			while ((line = br.readLine()) != null) {
				// 采集
				if(line.contains(dev)){
					content = line;
					//Log.d("content",  "content="+content);
					break;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// beginIndex
		int begin = content.indexOf(':');
		// endIndex
		//int end = content.indexOf('|');
		// 采集数量
		String tmp = null;
		tmp = content.substring(begin + 1, content.length());
		//transmit = content.substring(end + 1, content.length()-1);
		//og.d("content",  "receive="+tmp);
		String[] array, tmpArray ;
		tmpArray = new String[16];
		int counter = 0;
		array = tmp.split("\\ ");
		for(int i = 0; i < array.length; i++){
			//Log.d("content",  "array" + i+"="+array[i]);
			if(array[i].length() > 0){
				if(counter > tmpArray.length)break;
				tmpArray[counter] = array[i];
				//Log.d("content",  "tmpArray[" + counter + "]="  + tmpArray[counter]);
				counter++;
				
			}
		}
		
		// 转换为Int型
		//if(dev.equals("eth0")){
			mTotal[0] = Long.parseLong(tmpArray[0]);	
			mTotal[1] = Long.parseLong(tmpArray[8]);
		//}else if(dev.equals("wlan0")){
			//mTotal[0] = Long.parseLong(array[4]);	
			//mTotal[1] = Long.parseLong(array[51]);	
		//}
		//Log.d("content",  "mTotal0=" + mTotal[0]);
		//Log.d("content",  "mTotal1=" + mTotal[1]);
		return mTotal;
	}	

}
