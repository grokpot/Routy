package org.routy;

import org.routy.fragment.ErrorDialog;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class AppError {

	public static void showErrorDialog(FragmentActivity context, String message) {
    	FragmentManager fm = context.getSupportFragmentManager();
    	ErrorDialog errorDialog = new ErrorDialog(message);
    	errorDialog.show(fm, "fragment_error_message");
    }
}
