package iooojik.anon.meet.data.models

import iooojik.anon.meet.data.models.user.User

data class SeenModel(var seen: Boolean, var user: User = User())
