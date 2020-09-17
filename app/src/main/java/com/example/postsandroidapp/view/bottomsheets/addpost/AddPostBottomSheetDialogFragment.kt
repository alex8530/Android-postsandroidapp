package com.example.postsandroidapp.view.bottomsheets.addpost

import BaseBottomSheetFragment
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.example.postsandroidapp.R
import com.example.postsandroidapp.databinding.FragmentAddPostBottomSheetDialogBinding

interface AddEPostBottomSheetDialogFragmentDelegate {
    fun onPerformClick(name:String)
}

class AddPostBottomSheetDialogFragment:
BaseBottomSheetFragment<AddPostBottomSheetViewModel, FragmentAddPostBottomSheetDialogBinding>(
    AddPostBottomSheetViewModel::class.java
)  {

    override fun getLayoutRes(): Int {

        return R.layout.fragment_add_post_bottom_sheet_dialog
    }

    override fun initViewModel(): AddPostBottomSheetViewModel {
        return super.getViewModelFromFactory(null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (view?.parent as View).setBackgroundColor(Color.TRANSPARENT)
    }



    var delegateCallBack : AddEPostBottomSheetDialogFragmentDelegate?=null
    override fun initialize() {


    }

    override fun setParameter() {

    }

    override fun setupAction() {
        mBinding.sendAppCompatButton.setOnClickListener {
            delegateCallBack?.onPerformClick(
                mBinding.titleAppCompatEditText.text.toString()
            )
        }
    }

    override fun getData() {
    }

    override fun observeViewModel() {
    }


}