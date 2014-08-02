package com.sg.mtfont.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.sg.mtfont.MainActivity;

public class ApkInstallHelper {

	private static final String TAG = "ApkInstallHelper";

	public static Intent getIntentFromApk(File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		// startActivity(intent);
		return intent;
	}

	public static void unZip(Context context, String assetName,
			String outputDirectory) throws IOException {

		// 创建解压目标目录
		File file = new File(outputDirectory);

		// 如果目标目录不存在，则创建
		if (!file.exists()) {
			file.mkdirs();
		}

		InputStream inputStream = null;

		// 打开压缩文件
		inputStream = context.getAssets().open(assetName);

		ZipInputStream zipInputStream = new ZipInputStream(inputStream);

		// 读取一个进入点
		ZipEntry zipEntry = zipInputStream.getNextEntry();

		// 使用1Mbuffer
		byte[] buffer = new byte[1024 * 1024];

		// 解压时字节计数
		int count = 0;

		// 如果进入点为空说明已经遍历完所有压缩包中文件和目录
		while (zipEntry != null) {

			// 如果是一个目录
			if (zipEntry.isDirectory()) {
				file = new File(outputDirectory + File.separator
						+ zipEntry.getName());
				file.mkdir();
			} else {
				// 如果是文件
				file = new File(outputDirectory + File.separator
						+ zipEntry.getName());
				// 创建该文件
				file.createNewFile();
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				while ((count = zipInputStream.read(buffer)) > 0) {
					fileOutputStream.write(buffer, 0, count);
				}
				fileOutputStream.close();
			}

			// 定位到下一个文件入口
			zipEntry = zipInputStream.getNextEntry();
		}
		zipInputStream.close();

	}

	/**
	 * install slient
	 * 
	 * @param context
	 * @param filePath
	 * @return 0 means normal, 1 means file not exist, 2 means other exception
	 *         error
	 */
	public static int installSlient(Context context, String filePath) {
		File file = new File(filePath);
		if (filePath == null || filePath.length() == 0
				|| (file = new File(filePath)) == null || file.length() <= 0
				|| !file.exists() || !file.isFile()) {
			return 1;
		}

		String[] args = { "pm", "install", "-r", filePath };
		ProcessBuilder processBuilder = new ProcessBuilder(args);

		Process process = null;
		BufferedReader successResult = null;
		BufferedReader errorResult = null;
		StringBuilder successMsg = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		int result;
		try {
			process = processBuilder.start();
			successResult = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			errorResult = new BufferedReader(new InputStreamReader(
					process.getErrorStream()));
			String s;

			while ((s = successResult.readLine()) != null) {
				successMsg.append(s);
			}

			while ((s = errorResult.readLine()) != null) {
				errorMsg.append(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
			result = 2;
		} catch (Exception e) {
			e.printStackTrace();
			result = 2;
		} finally {
			try {
				if (successResult != null) {
					successResult.close();
				}
				if (errorResult != null) {
					errorResult.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (process != null) {
				process.destroy();
			}
		}

		// TODO should add memory is not enough here
		if (successMsg.toString().contains("Success")
				|| successMsg.toString().contains("success")) {
			result = 0;
		} else {
			result = 2;
		}
		Log.d("installSlient", "successMsg:" + successMsg + ", ErrorMsg:"
				+ errorMsg);
		return result;
	}

	public static boolean checkProgramInstalled(Context ctx, String packName) {
		boolean flag = false;
		PackageManager manager = ctx.getPackageManager();

		// 根据Intent值查询这样的app
		List<PackageInfo> infos = manager.getInstalledPackages(0);

		for (PackageInfo app : infos) {
			// 该应用的包名和主Activity
			String pkg = app.packageName;
			if (pkg.equals(packName)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * 
	 * 
	 * @param packageName
	 * @param path
	 *            2014年7月30日 下午11:32:26
	 */
	public static void silentInstall(Context context, String packageName,
			String path, Handler handler) {
		try {
			Uri uri = Uri.fromFile(new File(path));
			PackageManager pm = context.getPackageManager();
			pm.installPackage(uri, null, 0, packageName);
		} catch (SecurityException e) {
			Log.e(TAG, e.getMessage());
			if (handler != null) {
				handler.sendEmptyMessage(MainActivity.NO_INSTALL_PERMISSION);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 请求ROOT权限后执行命令（最好开启一个线程）
	 * 
	 * @param cmd
	 *            (pm install -r *.apk)
	 * @return
	 */
	public static boolean runRootCommand(String cmd) {
		Process process = null;
		DataOutputStream os = null;
		BufferedReader br = null;
		StringBuilder sb = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(cmd + "\n");
			os.writeBytes("exit\n");
			br = new BufferedReader(new InputStreamReader(
					process.getInputStream()));

			sb = new StringBuilder();
			String temp = null;
			while ((temp = br.readLine()) != null) {
				sb.append(temp + "\n");
				if ("Success".equalsIgnoreCase(temp)) {
					return true;
				}
			}
			process.waitFor();
		} catch (Exception e) {
		} finally {
			try {
				if (os != null) {
					os.flush();
					os.close();
				}
				if (br != null) {
					br.close();
				}
				process.destroy();
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}
}
