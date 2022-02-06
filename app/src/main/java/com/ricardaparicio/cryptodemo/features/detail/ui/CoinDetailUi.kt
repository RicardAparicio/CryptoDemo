package com.ricardaparicio.cryptodemo.features.detail.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.ricardaparicio.cryptodemo.R
import com.ricardaparicio.cryptodemo.core.util.Block
import com.ricardaparicio.cryptodemo.features.detail.ui.viewmodel.CoinDetailViewModel

@ExperimentalMaterialApi
@Composable
fun CoinDetailScreen(onBackClicked: Block) {
    val viewModel = hiltViewModel<CoinDetailViewModel>()
    CoinDetail(
        uiState = viewModel.uiState,
        onBackClicked = onBackClicked,
    )
}

@ExperimentalMaterialApi
@Composable
private fun CoinDetail(
    uiState: CoinDetailUiState,
    onBackClicked: Block,
) {
    val lazyListState = rememberLazyListState()

    val appBarVisibility = when (lazyListState.firstVisibleItemIndex) {
        0 -> lazyListState.firstVisibleItemScrollOffset > 400f
        else -> true
    }
    val appBarTitleVisibility = when (lazyListState.firstVisibleItemIndex) {
        0 -> lazyListState.firstVisibleItemScrollOffset > 500f
        else -> true
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
    ) {
        Content(
            uiState = uiState,
            lazyListState = lazyListState,
        )
        CollapsibleAppBar(
            title = uiState.coinSummary.name,
            appBarVisibility = appBarVisibility,
            titleVisibility = appBarTitleVisibility
        ) {
            onBackClicked()
        }
        if (!appBarVisibility) {
            FloatingBackIcon {
                onBackClicked()
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun FloatingBackIcon(
    onBackClicked: Block
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp)
            .height(dimensionResource(R.dimen.app_bar_height)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.clip(CircleShape),
            onClick = onBackClicked,
            color = Color.Transparent
        ) {
            Image(
                modifier = Modifier.padding(10.dp),
                painter = painterResource(R.drawable.ic_back),
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun CoinInfo(
    uiState: CoinDetailUiState,
) {
    Text(text = uiState.description)
}

@Composable
private fun Content(
    uiState: CoinDetailUiState,
    lazyListState: LazyListState,
) {
    Box {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background),
            state = lazyListState,
        ) {
            item {
                CoinImage(lazyListState, uiState.coinSummary.image)
            }
            item {
                CoinInfo(uiState)
            }
        }
    }
}

@Composable
fun CollapsibleAppBar(
    title: String,
    appBarVisibility: Boolean,
    titleVisibility: Boolean,
    onClickBack: Block
) {
    AnimatedVisibility(
        visible = appBarVisibility,
        enter = slideInVertically(),
        exit = ExitTransition.None
    ) {
        TopAppBar(
            title = {
                AnimatedVisibility(
                    visible = titleVisibility,
                    enter = slideInVertically(),
                    exit = slideOutVertically()
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.onPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onClickBack) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = null,
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
            },
            elevation = 8.dp,
        )
    }
}

@Composable
private fun CoinImage(
    lazyListState: LazyListState,
    imageUrl: String
) {
    val effectDivider = 8f
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(Color.Gray)
            .alpha(
                when (lazyListState.firstVisibleItemIndex) {
                    0 -> 1 - (lazyListState.firstVisibleItemScrollOffset / (effectDivider * 100))
                    else -> 0f
                }
            )
            .offset(
                y = when (lazyListState.firstVisibleItemIndex) {
                    0 -> (lazyListState.firstVisibleItemScrollOffset / effectDivider)
                    else -> 0f
                }.dp
            ),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            modifier = Modifier.size(140.dp),
            painter = rememberImagePainter(
                data = imageUrl,
                builder = {
                    crossfade(true)
                }
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
    }
}
