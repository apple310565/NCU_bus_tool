package flag.com.ncubus.ui.trainList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TrainListViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TrainListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is trainList fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}