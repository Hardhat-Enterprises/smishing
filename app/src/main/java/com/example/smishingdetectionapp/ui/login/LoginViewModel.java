package com.example.smishingdetectionapp.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.example.smishingdetectionapp.data.LoginRepository;
import com.example.smishingdetectionapp.data.Result;
import com.example.smishingdetectionapp.data.model.LoggedInUser;
import com.example.smishingdetectionapp.R;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final MutableLiveData<String> backupCodeResult = new MutableLiveData<>();
    private final LoginRepository loginRepository;

    public LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public LiveData<String> getBackupCodeResult() {
        return backupCodeResult;
    }

    // ------------------- LOGIN -------------------

    public void login(String username, String password) {
        Result<LoggedInUser> result = loginRepository.login(username, password);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }

    // ------------------- BACKUP CODES -------------------

    public void generateBackupCodes(String email) {
        loginRepository.generateBackupCodes(email, result -> {
            if (result instanceof Result.Success) {
                backupCodeResult.postValue("Backup codes generated successfully.");
            } else {
                backupCodeResult.postValue("Failed to generate backup codes.");
            }
        });
    }

    public void verifyBackupCode(String email, String code) {
        loginRepository.verifyBackupCode(email, code, result -> {
            if (result instanceof Result.Success) {
                backupCodeResult.postValue("Backup code verified successfully.");
            } else {
                backupCodeResult.postValue("Invalid or expired backup code.");
            }
        });
    }

    // ------------------- VALIDATION -------------------

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
