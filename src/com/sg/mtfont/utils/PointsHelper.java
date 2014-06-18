package com.sg.mtfont.utils;

import net.youmi.android.offers.PointsManager;
import android.content.Context;
import android.content.SharedPreferences;

public class PointsHelper {

	
	public static int sCurrPoints = -1;
	
	/**
	 * 奖励积分
	 * @param ctx
	 * @param points
	 */
	public static void awardPoints(Context ctx,int points){
		PointsManager.getInstance(ctx).awardPoints(points);
	}
	
	/**
	 * 消费积分
	 * @param ctx
	 * @param points
	 */
	public static void spendPoints(Context ctx,int points){
		PointsManager.getInstance(ctx).spendPoints(points);
	}
	
	
	/**
	 * 获取当前积分，如果没有联网，则取本地存储的
	 * @param ctx
	 * @return
	 */
	public static int getCurrentPoints(Context ctx){
		if (sCurrPoints == -1){
			SharedPreferences sp = ctx.getSharedPreferences(CommonUtils.FontXiu, Context.MODE_PRIVATE);
			sCurrPoints = sp.getInt(CommonUtils.CURRENT_POINTS, 0);
		}
		return sCurrPoints;
	}
}
