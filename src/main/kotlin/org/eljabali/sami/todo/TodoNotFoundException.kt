package org.eljabali.sami.todo

class TodoNotFoundException(id: Long) : RuntimeException("Post# $id is not existed.")