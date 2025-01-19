package com.techmania.newsapp.ui

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.techmania.newsapp.viewmodels.BaseViewModel

abstract class BaseActivity : AppCompatActivity() {
    var isAllPermissionsGranted = true
        private set

    private lateinit var progressDialog: ProgressDialog

    protected fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        isAllPermissionsGranted = true

        val permissions = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> listOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )

            Build.VERSION.SDK_INT <= Build.VERSION_CODES.S -> listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )

            else -> listOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

        for (permission in permissions) {
            if (
                ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
                isAllPermissionsGranted = false
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        isAllPermissionsGranted = true
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            grantResults.forEach {
                if (it != PackageManager.PERMISSION_GRANTED) {
                    isAllPermissionsGranted = false
                }
            }
        }
        if (!isAllPermissionsGranted) {
            showToast("Accept all required Permissions")
        }
    }

    protected open fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun initBaseVm(vm: BaseViewModel) {

        progressDialog = createProgressDialog(this, "Loading", "Please Wait..")

        vm.errorMessageLiveData.observe(this, ::showToast)

        vm.loadingLiveData.observe(this) {
            if (it) progressDialog.show() else progressDialog.dismiss()
        }
    }

    protected fun addFragment(id: Int, fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(id, fragment)
            .addToBackStack(fragment::class.simpleName)
            .commit()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1768
    }

    private fun createProgressDialog(mContext: Context?, title: String?, message: String?): ProgressDialog {
        val progressDialog = ProgressDialog(mContext)
        progressDialog.setMessage(message)
        progressDialog.setTitle(title)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        return progressDialog
    }
}