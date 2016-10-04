package com.delelong.diandiandriver.function;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class WeatherUtil {
	public static void Alert(Context context, String error) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(error)
         .setCancelable(false)
        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface dialog, int id) {
      	   dialog.cancel();
         }
     });
		 builder.show();
	}
}
