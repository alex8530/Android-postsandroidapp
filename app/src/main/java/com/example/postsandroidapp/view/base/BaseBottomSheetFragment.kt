
import CustomDialogLoading
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.postsandroidapp.viewmodels.base.BaseViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetFragment<VM : BaseViewModel, DB : ViewDataBinding>(
    private val mViewModelClass: Class<VM>
) : BottomSheetDialogFragment()  {
    lateinit var mViewModel: VM
    open lateinit var mBinding: DB

    @LayoutRes
    abstract fun getLayoutRes(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = initViewModel()
    }

    abstract fun initViewModel(): VM

    fun getViewModelFromFactory(creatorFactory: (() -> VM)? = null): VM {

        return if (creatorFactory == null) {
            ViewModelProviders.of(this).get(mViewModelClass)
        } else {
            ViewModelProviders.of(
                this,
                BaseViewModelFactory(
                    creatorFactory
                )
            )
                .get(mViewModelClass)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false)

        super.onCreateView(inflater, container, savedInstanceState)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //to init some data
        initialize()
        //to set parameters
        setParameter()
        //to set action listeners
        setupAction()
        //get data
        getData()
        //observe view model
        observeViewModel()

    }


    abstract fun initialize()
    abstract fun setParameter()
    abstract fun setupAction()
    abstract fun getData()
    abstract fun observeViewModel()

}