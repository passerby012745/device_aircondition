package com.szsbay.livehome.openlife.util;

import java.util.Map;

import org.json.JSONObject;

import com.huawei.smarthome.api.message.IDataService;
import com.huawei.smarthome.localapi.ServiceApi;
import com.huawei.smarthome.log.LogService;
import com.huawei.smarthome.log.LogServiceFactory;
import com.szsbay.livehome.openlife.bean.ServerAddress;

/**
 * <br>
 * 本地数据存储服务，用于从ont中存取配置数据 <br>
 */
public class DataService
{
    /**
     * <br>
     * 日志对象
     */
    private static final LogService logger = LogServiceFactory.getLogService(DataService.class);

    /**
     * <br>
     * 配置数据key
     */
    private static final String CONFIGURATION_SERVER = "configuratioServerData";
    /**
     * <br>
     * 网关本地数据存储服务
     * @return The data service
     */
    private static IDataService getDataService()
    {
        return ServiceApi.getService(IDataService.class, null);
    }

    /**
     * <br>
     * 从存储获取信息
     * @return String 服务器url
     */
    public static ServerAddress getServerConfig()
    {
        IDataService dataService = getDataService();
        String serverUrl=null;
        if (null == dataService)
        {
            logger.i("getServerConfig(), dataService is null.");
            return null;
        }

        // if no data saved return null
        Map<String, JSONObject> list = dataService.list();
        if (null == list || list.get(CONFIGURATION_SERVER) == null)
        {
            logger.i("getServerConfig(), configurationData is null.");
            return null;
        }
        JSONObject configObject = list.get(CONFIGURATION_SERVER);
        ServerAddress serverAddress =new ServerAddress();
        serverAddress.setPort(configObject.optInt("port",0));
        serverAddress.setAddress(configObject.optString("address",""));
        serverAddress.setDevice_url(configObject.optString("device_url",""));
        serverAddress.setRoom_url(configObject.optString("room_url",""));
        serverAddress.setScene_url(configObject.optString("scene_url",""));
        return serverAddress;
    }
    /**
     * <br>
     * 将信息保存到存储
     * @return String 服务器url
     */
    public static void putServerConfig(ServerAddress serverAddress)
    {
        IDataService dataService = getDataService();
        if (null == dataService)
        {
            logger.i("putServerConfig(), dataService is null.");
            return ;
        }

        JSONObject paramObject= new JSONObject();
        paramObject.put("port",serverAddress.getPort());
        paramObject.put("address",serverAddress.getAddress());
        paramObject.put("device_url",serverAddress.getDevice_url());
        paramObject.put("room_url",serverAddress.getRoom_url());
        paramObject.put("scene_url",serverAddress.getScene_url());
		dataService.put(CONFIGURATION_SERVER, paramObject);
    }
}
