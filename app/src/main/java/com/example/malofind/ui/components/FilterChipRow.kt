package com.example.malofind.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.malofind.ui.theme.MaloFindGreen
import com.example.malofind.ui.theme.MaloFindTextPrimary
import com.example.malofind.ui.theme.MaloFindTextSecondary

@Composable
fun FilterChipRow(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    filters: List<String> = listOf(
        "All",
        "For Rent",
        "For Sale",
        "House",
        "Apartment",
        "Room"
    )
) {
    Row(
        modifier = Modifier.horizontalScroll(
            rememberScrollState()
        ),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        filters.forEach { filter ->

            val selected = selectedFilter == filter

            FilterChip(
                selected = selected,
                onClick = {
                    onFilterSelected(filter)
                },
                label = {
                    Text(
                        text = filter
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaloFindGreen,
                    selectedLabelColor = androidx.compose.ui.graphics.Color.White,
                    containerColor = androidx.compose.ui.graphics.Color.White,
                    labelColor = MaloFindTextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected,
                    borderColor = MaloFindTextSecondary.copy(alpha = 0.30f),
                    selectedBorderColor = MaloFindGreen
                )
            )
        }
    }
}