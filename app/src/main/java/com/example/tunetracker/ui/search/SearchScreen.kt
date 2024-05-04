package com.example.tunetracker.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tunetracker.data.local.model.Audio
import com.example.tunetracker.ui.audio.AudioItem
import com.example.tunetracker.ui.player.noRippleClickable

@Preview(showSystemUi = true, showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onSongClicked: (Audio) -> Unit = {}
) {
    val searchText by viewModel.searchText.collectAsState()
    val songs by viewModel.songs.collectAsState(initial = emptyList())
    var active by remember {
        mutableStateOf(false)
    }
    Column {
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            query = searchText,
            onQueryChange = viewModel::onSearchTextChange,
            onSearch = {
                active = false
                viewModel.searchTracks()
            },
            active = active,
            onActiveChange = { active = it },
            placeholder = { Text(text = "What do you want to listen") },
            leadingIcon = {
                if (active) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable { active = false })
                } else {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                }
            },
            trailingIcon = {
                if (active && searchText.isNotBlank()) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.noRippleClickable {
                            viewModel.clearSearchText()
                        })
                }
            }
        ) {}
        if (viewModel.isSearching.collectAsState().value) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        } else {
            if (songs == emptyList<Audio>() && searchText.isNotBlank()) {
                Text(
                    text = "Couldn't find $searchText try different word",
                    modifier = Modifier.fillMaxSize(),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn(contentPadding = PaddingValues(8.dp)) {
                    itemsIndexed(items = songs) { _, song ->
                        AudioItem(audio = song, onItemClick = { onSongClicked(song) })
                    }
                }
            }

        }

    }

}