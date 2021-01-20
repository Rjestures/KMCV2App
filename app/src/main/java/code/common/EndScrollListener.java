package code.common;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created method mohammadfaiz on 28/08/17.
 */

public abstract class EndScrollListener extends RecyclerView.OnScrollListener {

    public static String TAG = EndScrollListener.class.getSimpleName();

    // use your LayoutManager instead
    private LinearLayoutManager llm;

    public EndScrollListener(LinearLayoutManager sglm) {
        this.llm = llm;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (!recyclerView.canScrollVertically(1)) {
            onScrolledToEnd();
        }
    }

    public abstract void onScrolledToEnd();
}
