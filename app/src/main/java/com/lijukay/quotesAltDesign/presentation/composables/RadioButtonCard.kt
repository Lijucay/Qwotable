package com.lijukay.quotesAltDesign.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun RadioButtonCard(
    modifier: Modifier = Modifier,
    selected: Boolean,
    title: String,
    shape: Shape = CardDefaults.shape,
    description: String? = null,
    onClick: () -> Unit
) {
    Card(
        shape = shape,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 56.dp)
                .selectable(
                    selected = selected,
                    role = Role.RadioButton,
                    onClick = onClick
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                modifier = Modifier.padding(start = 16.dp, end = 8.dp),
                selected = selected,
                onClick = null
            )

            Column(
                modifier = Modifier.padding(
                    start = 8.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 16.dp
                )
            ) {
                Text(text = title)

                AnimatedVisibility(visible = selected) {
                    description?.let {
                        Text(text = description)
                    }
                }
            }
        }
    }
}