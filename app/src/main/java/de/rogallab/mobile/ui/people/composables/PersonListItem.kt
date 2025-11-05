package de.rogallab.mobile.ui.people.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import de.rogallab.mobile.domain.utilities.logComp

@Composable
fun PersonListItem(
   id: String,                // State ↓
   firstName: String,         // State ↓
   lastName: String,          // State ↓
   email: String,             // State ↓
   phone: String,             // State ↓
   imagePath: String? = null, // State ↓
   onClicked: () -> Unit,     // Event ↑
   onDeleted: () -> Unit      // Event ↑
) {
   val tag = "<-PersonListItem"
   val nComp = remember { mutableIntStateOf(1) }
   SideEffect { logComp(tag, "Composition #${nComp.intValue++}") }

   Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
         .padding(vertical = 4.dp)
         .fillMaxWidth()
         .background(MaterialTheme.colorScheme.inverseOnSurface)
         .clickable { onClicked() }
   ) {
      imagePath?.let { path: String ->
         Column(modifier = Modifier.weight(0.2f)) {
            AsyncImage(
               model = path,
               contentDescription = "Bild der Person",
               modifier = Modifier
                  .size(width = 60.dp, height = 75.dp)
                  .clip(RoundedCornerShape(percent = 15))
                  .padding(all = 4.dp),
               alignment = Alignment.Companion.Center,
               contentScale = ContentScale.Companion.Crop
            )
         }
      }

      Column(modifier = Modifier.weight(0.8f)
         .padding(start = 4.dp)
         .padding(vertical = 2.dp)

      ) {

         Text(
            text = "$firstName $lastName",
            style = MaterialTheme.typography.bodyLarge
         )
         Text(
            text = email,
            style = MaterialTheme.typography.bodySmall
         )
         Text(
            text = phone,
            style = MaterialTheme.typography.bodySmall
         )
      } // Column

      IconButton(
         onClick = { onDeleted() }, // Event ↑
         modifier = Modifier.padding(end = 4.dp)
            .weight(0.1f)
      ) {
         Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "Delete item"
         )
      }
   }
}
