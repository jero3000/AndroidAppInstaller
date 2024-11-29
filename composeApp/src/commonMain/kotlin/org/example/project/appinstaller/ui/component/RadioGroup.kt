package org.example.project.appinstaller.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

data class RadioEntry(val name: String, val tooltip: String, val key: String)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RadioGroup(modifier: Modifier = Modifier,
               title: String? = null,
               radioOptions : List<RadioEntry>,
               selectedOption: RadioEntry,
               onSelected : (option:RadioEntry ) -> Unit) {
    // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
    Column(modifier = modifier.selectableGroup()) {
        if(title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 16.dp, top = 10.dp, bottom = 5.dp)
            )
        }
        radioOptions.forEachIndexed { index, entry ->
            TooltipArea(
                tooltip = {
                    // Composable tooltip content:
                    Surface(
                        modifier = Modifier.shadow(4.dp),
                        color = Color(255, 255, 210),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = entry.tooltip,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                },
                delayMillis = 600, // In milliseconds
                tooltipPlacement = TooltipPlacement.CursorPoint(
                    alignment = Alignment.BottomEnd,
                    offset = if (index % 2 == 0) DpOffset(
                        (-16).dp,
                        0.dp
                    ) else DpOffset.Zero // Tooltip offset
                )
            ) {
                Row(
                    Modifier.fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (entry == selectedOption),
                            onClick = {
                                onSelected(entry)
                            },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (entry == selectedOption),
                        onClick = null // null recommended for accessibility with screenreaders
                    )
                    Text(
                        text = entry.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}
