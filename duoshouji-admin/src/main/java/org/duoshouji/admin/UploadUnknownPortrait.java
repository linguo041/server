package org.duoshouji.admin;

import com.aliyun.oss.OSSClient;

public class UploadUnknownPortrait {
	
	private static final String ACCESS_KEY_ID = "REQ4WWCYXaNWqTBZ";
	private static final String ACCESS_KEY_SECRET = "aC1RtnoiQ4xj96R7U4nFfJPxO9tSeT";
	private static final String ALIYUN_END_POINT = "oss-cn-shanghai.aliyuncs.com";
	private static final String BUCKET_NAME = "duoshouji-test";
	
	public static void main(String[] args) throws Exception  {
		OSSClient client = new OSSClient(ALIYUN_END_POINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
		client.putObject(BUCKET_NAME, "images/common/unknown-portrait.jpg"
				, UploadUnknownPortrait.class.getResourceAsStream("/unknown-portrait.jpg"));
		client.shutdown();
	}
}
