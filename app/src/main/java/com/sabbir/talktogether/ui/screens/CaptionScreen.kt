package com.sabbir.talktogether.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sabbir.talktogether.data.model.CaptionTurn
import com.sabbir.talktogether.viewmodel.CaptionViewModel
import kotlinx.coroutines.launch

@Composable
fun CaptionScreen(viewModel: CaptionViewModel = viewModel()) {

    val turns by viewModel.turns.collectAsState()
    val partialText by viewModel.partialText.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Auto scroll to bottom when a new turn arrives
    LaunchedEffect(turns.size) {
        if (turns.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(turns.size - 1)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A)) // dark background
            .padding(16.dp)
    ) {

        // ── Top bar ──────────────────────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Green dot + "CAPTIONS ON" badge
            if (isListening) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color(0xFF4CAF50), CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "CAPTIONS ON",
                    color = Color(0xFF4CAF50),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Start / Stop button
            Button(
                onClick = {
                    if (isListening) viewModel.stopListening()
                    else viewModel.startListening()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isListening) Color(0xFFE53935) else Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (isListening) "Stop" else "Start",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Caption turns list ────────────────────────────────────
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(turns, key = { it.id }) { turn ->
                CaptionTurnCard(turn = turn)
            }

            // Live partial text at the bottom while speaking
            if (partialText.isNotEmpty()) {
                item {
                    Text(
                        text = partialText,
                        color = Color(0xFF9E9E9E),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CaptionTurnCard(turn: CaptionTurn) {
    val backgroundColor = if (turn.isQuestion) Color(0xFF1A3A5C) else Color(0xFF2C2C2C)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (turn.isQuestion) {
                Text(
                    text = "❓ Question",
                    color = Color(0xFF64B5F6),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = turn.text,
                color = Color.White,
                fontSize = 22.sp,
                lineHeight = 30.sp
            )
        }
    }
}