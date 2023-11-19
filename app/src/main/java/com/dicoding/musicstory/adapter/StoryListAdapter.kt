import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.musicstory.R
import com.dicoding.musicstory.response.Story
import com.dicoding.musicstory.constants.Constants
import com.dicoding.musicstory.databinding.StoryLayoutBinding
import com.dicoding.musicstory.ui.detailstory.DetailStoryAct
import com.dicoding.musicstory.utils.withDateFormat

class StoryListAdapter : PagingDataAdapter<Story, StoryListAdapter.ListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        return ListViewHolder(
            StoryLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        data?.let { holder.bind(it) }
    }

    inner class ListViewHolder(private val binding: StoryLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) {
            with(binding) {
                nameTextView.text = story.name
                dateTextView.text = story.createdAt.withDateFormat()
                descriptionTextView.text = story.description
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .fitCenter()
                    .into(storyImageView)

                storyCardView.setOnClickListener {
                    val progressBarLayout = LayoutInflater.from(itemView.context).inflate(R.layout.loading_layout, null)
                    val progressBar = progressBarLayout.findViewById<ProgressBar>(R.id.progressBar)

                    binding.root.addView(progressBarLayout)

                    Handler().postDelayed({

                        val intent = Intent(itemView.context, DetailStoryAct::class.java)
                        intent.putExtra(Constants.DETAIL_STORY, story)
                        itemView.context.startActivity(intent)

                        // Hapus ProgressBar dari tata letak setelah proses pemrosesan selesai
                        binding.root.removeView(progressBarLayout)
                    }, 125)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }
        }
    }
}
