package com.szsbay.livehome.openlife.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONArray;

import com.huawei.smarthome.localapi.LocalFileApi;
import com.huawei.smarthome.log.LogService;
import com.huawei.smarthome.log.LogServiceFactory;

/**
 * <div class="English"> store the device list to file and read the device list from file </div> <br> 
 * <div class="Chinese"> 用来将设备存储到文件和从文件读取出设备 </div> <br>
 * 
 * @author niehehua
 * @since Openlife SDK 2.0 2015年10月9日
 */
public class DeviceStorage
{
    /**
     * <div class="English">Log service for this class </div>
     * <div class="Chinese">日志服务 </div>
     */
    private final static LogService logger = LogServiceFactory.getLogService(DeviceStorage.class);

    /**
     * <div class="English"> store the device list to file</div> 
     * <div class="Chinese"> 将设备列表存储进文件</div>
     * 
     * @param appConfigFileName  The config file name for app
     * @param jsonArray The JSON data array of devices
     */
    public void putDevice2File(String appConfigFileName, JSONArray jsonArray)
    {
        File file = LocalFileApi.getDataFile(appConfigFileName + ".json");

        logger.d("Absolute file Path = {}" ,file.getAbsolutePath());
		
        BufferedWriter bufferedWriter = null;
        try
        {
            if(!file.exists())
            {
                file.createNewFile();
            }
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(jsonArray.toString());
            bufferedWriter.flush();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        finally
        {
            close(bufferedWriter);
        }
    }

    /**
     * <div class="English"> read the device list from file</div> <br>
     * <div class="Chinese"> 从文件中读取设备列表 </div> <br>
     * 
     * @param appConfigFileName  The config file name for app
     * @return The JSON data array of devices
     */
    public JSONArray getDeviInfofromFile(String appConfigFileName)
    {
        File file = LocalFileApi.getDataFile(appConfigFileName + ".json");

        if(!file.exists())
        {
            logger.e("The file {0} is not exist !!! ", file);
            return null;
        }

        StringBuilder builder = new StringBuilder();
        BufferedReader input = null;
        JSONArray jsonlist = null;

        try
        {
            input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            String line = null;
            while((line = input.readLine()) != null)
            {
                builder.append(line);
            }

            jsonlist = new JSONArray(builder.toString());

            //input.close();

            logger.d("File read successfully, JSON count is {0}, File={1}",
                jsonlist.length(), file);
        }
        catch(Exception e)
        {
            logger.e("Failed to read file", e);
        }
        finally
        {
            close(input);
        }

        return jsonlist;
    }

    /**
     * Close a stream
     * 
     * @param c The stream to be closed
     */
    private void close(Closeable c)
    {
        if(c != null)
        {
            try
            {
                c.close();
            }
            catch(IOException e)
            {
                logger.e("Failed to close stream", e);
            }
        }
    }
}
