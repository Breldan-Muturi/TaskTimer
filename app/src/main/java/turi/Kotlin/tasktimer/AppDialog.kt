package turi.Kotlin.tasktimer

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDialogFragment
import java.lang.ClassCastException
import java.lang.IllegalArgumentException

private const val TAG = "AppDialog"
const val DIALOG_ID = "id"
const val DIALOG_MESSAGE = "message"
const val DIALOG_POSITIVE_RID = "positive rid"
const val DIALOG_NEGATIVE_RID = "negative rid"

class AppDialog : AppCompatDialogFragment() {

    private var dialogEvents: DialogEvents? = null

    /**
     * The dialog's callback interface, to notify of user selected results ( deletion confirmed etc)     *
     */
    internal interface DialogEvents {
        fun onPositiveDialogResult(dialogId: Int, args: Bundle)
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach called: context is $context")
        super.onAttach(context)
        //Activities/Fragments containing this fragments must implement it's callbacks
        dialogEvents = try {
            //Is there a parent fragment , if so that will be what we callback
            parentFragment as DialogEvents
        }
        catch (e: TypeCastException) {
            try {
                //No parent fragment, so callack the activity instead
                context as DialogEvents
            }
            catch (e: ClassCastException) {
                //Activity doesn't implement the interface
                throw ClassCastException("Activity $context must implement AppDialog.DialogEvents interface")
            }
        }
        catch (e: ClassCastException) {
//            Parent doesn't implement the interface
            throw ClassCastException ("Fragment $parentFragment must implement AppDialog.DialogEvents interface")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d(TAG,"onCreateDialog: called")
        val builder = AlertDialog.Builder(context!!)
        //fix "smart cast to Bundle is impossible, since 'arguments' is a mutable property that could have been changed by this time
        val arguments = arguments
        val dialogId: Int
        val messageString: String?
        var positiveStringId : Int
        var negativeStringId : Int

        if ( arguments != null ) {
            dialogId = arguments.getInt(DIALOG_ID)
            messageString = arguments.getString(DIALOG_MESSAGE)
            if (dialogId == 0 || messageString == null) {
                throw IllegalArgumentException("DIALOG_ID and/ or DIALOG_MESSAGE are not present in the Bundle")
            }
            positiveStringId = arguments.getInt(DIALOG_POSITIVE_RID)

            if ( positiveStringId == 0) {
                positiveStringId = R.string.ok
            }
           negativeStringId = arguments.getInt(DIALOG_NEGATIVE_RID)

            if ( negativeStringId == 0) {
                negativeStringId = R.string.cancel
            }
        } else {
            throw IllegalArgumentException("Must pass DIALOG_ID and DIALOG_MESSAGE in the Bundle")
        }
        return builder.setMessage(messageString)
            .setPositiveButton(positiveStringId) { dialogInterface, which ->  
                //Call positive result function
                dialogEvents?.onPositiveDialogResult(dialogId, arguments)
            }
            .setNegativeButton(negativeStringId) { dialogInterface, which ->
                //Call negative result function, if you want to implement it
                //dialogEvents?.onNegativeDialogResult(dialogId, arguments)
            }
            .create()
    }

    override fun onDetach() {
        Log.d(TAG, "onDetach called")
        //Reset the activities callback interface because we're no longer attached
        dialogEvents = null
        super.onDetach()
    }

    override fun onCancel(dialog: DialogInterface) {
        Log.d(TAG,"onCancel: called")
        val dialogId = arguments!!.getInt(DIALOG_ID)
//        dialogEvents?.onDialogCancelled(dialogId)
    }

}