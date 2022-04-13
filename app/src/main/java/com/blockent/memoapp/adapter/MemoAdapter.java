package com.blockent.memoapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.blockent.memoapp.R;
import com.blockent.memoapp.model.Memo;
import com.blockent.memoapp.utils.Utils;
import com.bumptech.glide.Glide;

import java.util.List;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.ViewHolder> {

    // 리사이클러뷰에서, 클릭이벤트 처리할때는 아래 코드를 그냥 카피해서 사용
    public interface OnItemClickListener{
        void onItemClick(int index);
        void onDeleteClick(int index);
    }

    OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
    //////////////////////////////////////////////////////////////////

    Context context;
    List<Memo> memoList;

    public MemoAdapter(Context context, List<Memo> memoList) {
        this.context = context;
        this.memoList = memoList;
    }

    @NonNull
    @Override
    public MemoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.memo_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoAdapter.ViewHolder holder, int position) {
        // 자바의 리스트에 들어있는 데이터와, 화면을 연결시키는 역할.
        Memo memo = memoList.get(position);

        holder.txtTitle.setText(  memo.getTitle() );
        holder.txtContent.setText( memo.getContent() );
        holder.txtDate.setText( memo.getDate() );
        if( memo.getPhoto_url() != null ){
            Glide.with(context).load(Utils.IMAGE_URL+memo.getPhoto_url())
                    .into(holder.imgPhoto);
        }else {
            holder.imgPhoto.setImageResource(R.drawable.ic_calendar);
        }
    }

    @Override
    public int getItemCount() {
        return memoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

                private TextView txtTitle;
                private TextView txtContent;
                private TextView txtDate;
                private CardView cardView;
                private ImageView imgDelete;
                private ImageView imgPhoto;

                public ViewHolder(@NonNull View itemView) {
                    super(itemView);
                    txtTitle = itemView.findViewById(R.id.txtTitle);
                    txtContent = itemView.findViewById(R.id.txtContent);
                    txtDate =itemView.findViewById(R.id.txtDate);
                    imgDelete = itemView.findViewById(R.id.imgDelete);
                    cardView = itemView.findViewById(R.id.cardView);
                    imgPhoto = itemView.findViewById(R.id.imgPhoto);

                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // 아래 코드는 카피 앤 페이스트해서 사용!
                            int index = getAdapterPosition();
                            if(listener != null){
                                listener.onItemClick(index);
                            }
                            ///////////////////////////////////
                        }
                    });
                    imgDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int index = getAdapterPosition();
                            if(listener != null){
                                listener.onDeleteClick(index);
                            }
                        }
                    });

        }
    }
}
