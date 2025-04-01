@Composable
fun UserProfileSection(
    user: User,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimensions.spacing.medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = user.profilePicture,
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.profile_placeholder)
                )

                Spacer(modifier = Modifier.width(Dimensions.spacing.medium))

                Column {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onEditProfile) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "Edit profile"
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimensions.spacing.large))

        // Estadísticas del usuario
        UserStatsRow(user.stats)

        Spacer(modifier = Modifier.height(Dimensions.spacing.large))

        // Botón de cierre de sesión
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Icon(
                imageVector = Icons.Rounded.Logout,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(Dimensions.spacing.small))
            Text("Sign Out")
        }
    }
}

@Composable
private fun UserStatsRow(stats: UserStats) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(
            value = stats.totalMeditationMinutes.toString(),
            label = "Minutes\nMeditated",
            icon = Icons.Rounded.Timer
        )
        StatItem(
            value = stats.currentStreak.toString(),
            label = "Day\nStreak",
            icon = Icons.Rounded.LocalFire
        )
        StatItem(
            value = stats.completedSessions.toString(),
            label = "Sessions\nCompleted",
            icon = Icons.Rounded.CheckCircle
        )
    }
} 