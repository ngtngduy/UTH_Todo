package com.example.uthtodo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uthtodo.model.MockData
import com.example.uthtodo.model.SubTask
import com.example.uthtodo.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class TaskViewModel : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(MockData.getInitialTasks())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Tất cả")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // 1. Thống kê cho màn hình Trang chủ (HomeScreen)
    val todayTasks: StateFlow<List<Task>> = _tasks.map { list ->
        list.filter { it.date == "Hôm nay" }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Số việc hoàn thành / tổng số việc hôm nay
    val todayDoneCount: StateFlow<Int> = _tasks.map { list ->
        list.count { it.date == "Hôm nay" && it.done }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val todayTotalCount: StateFlow<Int> = _tasks.map { list ->
        list.count { it.date == "Hôm nay" }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    // Đếm việc còn lại theo danh mục
    val remainingStudyCount: StateFlow<Int> = _tasks.map { list ->
        list.count { it.category == "study" && !it.done }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val remainingLifeCount: StateFlow<Int> = _tasks.map { list ->
        list.count { it.category == "life" && !it.done }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    // Tổng số việc chưa xong (cho màn Danh sách)
    val uncompletedCount: StateFlow<Int> = _tasks.map { list ->
        list.count { !it.done }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    // Thống kê tổng quan cho màn hình Hồ sơ (ProfileScreen)
    val totalDoneCount: StateFlow<Int> = _tasks.map { list ->
        list.count { it.done }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val totalPendingCount: StateFlow<Int> = _tasks.map { list ->
        list.count { !it.done }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val completionRate: StateFlow<Int> = _tasks.map { list ->
        if (list.isNotEmpty()) (list.count { it.done } * 100 / list.size) else 0
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    // Danh sách sau khi lọc và tìm kiếm
    val filteredTasks: StateFlow<List<Task>> = combine(_tasks, _searchQuery, _selectedCategory) { list, query, category ->
        list.filter { task ->
            val matchQuery = query.isBlank() || task.title.contains(query, ignoreCase = true) || task.note.contains(query, ignoreCase = true)
            val matchCategory = when (category) {
                "Học tập" -> task.category == "study"
                "Cuộc sống" -> task.category == "life"
                else -> true
            }
            matchQuery && matchCategory
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Các hàm tương tác & cập nhật dữ liệu
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun toggleTaskDone(taskId: Int) {
        _tasks.update { list ->
            list.map { task ->
                if (task.id == taskId) {
                    val newDone = !task.done
                    task.copy(done = newDone)
                } else {
                    task
                }
            }
        }
    }

    fun deleteTask(taskId: Int) {
        _tasks.update { list ->
            list.filterNot { it.id == taskId }
        }
    }

    fun toggleSubTaskDone(taskId: Int, subTaskId: Int) {
        _tasks.update { list ->
            list.map { task ->
                if (task.id == taskId) {
                    val updatedSubtasks = task.subtasks.map { sub ->
                        if (sub.id == subTaskId) sub.copy(done = !sub.done) else sub
                    }
                    task.copy(subtasks = updatedSubtasks)
                } else {
                    task
                }
            }
        }
    }

    fun addSubTask(taskId: Int, title: String) {
        if (title.isBlank()) return
        _tasks.update { list ->
            list.map { task ->
                if (task.id == taskId) {
                    val newId = (task.subtasks.maxOfOrNull { it.id } ?: 0) + 1
                    task.copy(subtasks = task.subtasks + SubTask(id = newId, title = title.trim(), done = false))
                } else {
                    task
                }
            }
        }
    }

    fun deleteSubTask(taskId: Int, subTaskId: Int) {
        _tasks.update { list ->
            list.map { task ->
                if (task.id == taskId) {
                    task.copy(subtasks = task.subtasks.filterNot { it.id == subTaskId })
                } else {
                    task
                }
            }
        }
    }

    fun addNewTask(
        title: String,
        category: String,
        time: String = "19:00",
        date: String = "Hôm nay",
        priority: String = "medium",
        reminder: String = "Trước 15 phút",
        note: String = ""
    ) {
        if (title.isBlank()) return
        val newId = (_tasks.value.maxOfOrNull { it.id } ?: 0) + 1
        val newTask = Task(
            id = newId,
            title = title.trim(),
            note = note.trim(),
            category = category,
            date = date,
            time = time,
            priority = priority,
            reminder = reminder,
            done = false
        )
        _tasks.update { listOf(newTask) + it }
    }

    fun getTask(taskId: Int): Task? {
        return _tasks.value.find { it.id == taskId }
    }

    fun resetToMockData() {
        _tasks.value = MockData.getInitialTasks()
    }
}
