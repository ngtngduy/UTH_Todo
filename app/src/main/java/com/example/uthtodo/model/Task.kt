package com.example.uthtodo.model

data class SubTask(
    val id: Int,
    val title: String,
    val done: Boolean = false
)

data class Task(
    val id: Int,
    val title: String,
    val note: String = "",
    val category: String, // "study" hoặc "life"
    val date: String = "Hôm nay",
    val time: String = "12:00",
    val priority: String = "medium", // "high", "medium", "low"
    val done: Boolean = false,
    val reminder: String = "Trước 15 phút",
    val subtasks: List<SubTask> = emptyList()
) {
    val categoryDisplayName: String
        get() = if (category == "study") "Học tập" else "Cuộc sống"

    val isHighPriority: Boolean
        get() = priority.equals("high", ignoreCase = true)
}

object MockData {
    fun getInitialTasks(): List<Task> = listOf(
        Task(
            id = 1,
            title = "Tập thể dục 30 phút",
            category = "life",
            date = "Hôm nay",
            time = "06:30",
            done = true,
            priority = "low",
            subtasks = listOf(
                SubTask(1, "Chạy bộ 15 phút", true),
                SubTask(2, "Hít đất và gập bụng", true)
            )
        ),
        Task(
            id = 2,
            title = "Nộp bài tập Lập trình di động",
            category = "study",
            date = "Hôm nay",
            time = "09:00",
            done = true,
            priority = "medium",
            subtasks = listOf(
                SubTask(1, "Kiểm tra lại code Compose", true),
                SubTask(2, "Nén file zip gửi thầy", true)
            )
        ),
        Task(
            id = 3,
            title = "Ôn chương 3 – Cấu trúc dữ liệu",
            note = "Tập trung phần cây nhị phân và bảng băm.",
            category = "study",
            date = "Hôm nay",
            time = "14:00",
            priority = "high",
            done = false,
            reminder = "Trước 15 phút",
            subtasks = listOf(
                SubTask(1, "Đọc lại slide bài giảng", true),
                SubTask(2, "Làm 10 bài tập cuối chương", false)
            )
        ),
        Task(
            id = 4,
            title = "Đi chợ mua đồ cho tuần",
            category = "life",
            date = "Hôm nay",
            time = "17:30",
            done = false,
            priority = "medium",
            subtasks = emptyList()
        ),
        Task(
            id = 5,
            title = "Gọi điện về nhà",
            category = "life",
            date = "Ngày mai",
            time = "21:00",
            done = false,
            priority = "medium",
            subtasks = emptyList()
        ),
        Task(
            id = 6,
            title = "Họp nhóm đồ án",
            category = "study",
            date = "Tuần này",
            time = "20:00",
            priority = "high",
            done = false,
            subtasks = listOf(
                SubTask(1, "Phân công nhiệm vụ sprint", false)
            )
        )
    )
}
