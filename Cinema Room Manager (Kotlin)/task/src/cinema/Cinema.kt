package cinema

fun main() {
    val cinema = userInitCinema()
    menuInput(cinema)
}

private fun menuInput(cinema: Cinema) {
    println("\n1. Show the seats")
    println("2. Buy a ticket")
    println("3. Statistics")
    println("0. Exit")
    val menuInput = readln().toInt()
    when (menuInput) {
        1 -> {
            println(cinema)
            menuInput(cinema)
        }

        2 -> {
            buyTicket(cinema)
            menuInput(cinema)
        }

        3 -> {
            stats(cinema)
            menuInput(cinema)
        }

        0 -> {}

        else -> {
            println("Invalid Input")
            menuInput(cinema)
        }
    }
}

private fun stats(cinema: Cinema) {
    val nbSoldTickets = cinema.nbSoldTickets()
    val percentSold = nbSoldTickets.toDouble() * 100 / (cinema.rows * cinema.rowSeats)

    println("\nNumber of purchased tickets: $nbSoldTickets")
    println("Percentage: ${"%.2f".format(percentSold)}%")
    println("Current income: \$${cinema.calcCurrentIncome()}")
    println("Total income: \$${cinema.calcTotalIncome()}")

}

private fun buyTicket(cinema: Cinema) {
    val rowNumber = println("\nEnter a row number:").let { readln().toInt() }
    val seatInRow = println("Enter a seat number in that row:").let { readln().toInt() }
    when {
        rowNumber > cinema.rows || seatInRow > cinema.rowSeats -> {
            println("\nWrong input!")
            buyTicket(cinema)
        }
        !cinema.isAvailable(rowNumber, seatInRow) -> {
            println("\nThat ticket has already been purchased!")
            buyTicket(cinema)
        }
        cinema.isAvailable(rowNumber, seatInRow) -> {
            println("\nTicket price: $${cinema.seatPrice(rowNumber)}")
            cinema.markSeatSold(rowNumber, seatInRow)
        }
    }
}

private fun userInitCinema(): Cinema {
    val rows = println("\nEnter the number of rows:").let { readln().toInt() }
    val seatsPerRow = println("Enter the number of seats in each row:").let { readln().toInt() }
    return Cinema(rows, seatsPerRow)
}


data class Cinema(val rows: Int, val rowSeats: Int) {
    private var seats = Array(rows) { Array(rowSeats) { "S" } }

    fun isAvailable(row: Int, seatInRow: Int): Boolean = seats[row-1][seatInRow-1] == "S"

    fun seatPrice(row: Int): Int {
        if (rows * rowSeats > 60 && row > rows / 2)
            return 8
        return 10
    }

    fun markSeatSold(rowNumber: Int, seatInRow: Int): Boolean {
        if (seats[rowNumber - 1][seatInRow - 1] == "S") {
            seats[rowNumber - 1][seatInRow - 1] = "B"
            return true
        } else
            return false
    }

    fun calcCurrentIncome(): Int = seats.flatMapIndexed { i, row ->
        row.map { seat ->
            if (seat == "B")
                seatPrice(i+1)
            else
                0
        }
    }.sum()

    fun calcTotalIncome(): Int {
        val totalSeats = rows * rowSeats
        if (totalSeats <= 60) {
            return totalSeats * 10
        } else {
            val frontHalf = rows / 2
            return frontHalf * rowSeats * 10 + (rows - frontHalf) * rowSeats * 8
        }
    }

    override fun toString(): String {
        val title = "Cinema: "
        val seatsHeader = (0..rowSeats).map { if (it == 0) " " else it }.joinToString(" ")
        val rowsSeats = seats.mapIndexed { index, row ->
            "${index + 1} " + row.joinToString(" ")
        }.joinToString("\n")
        return "\n$title\n$seatsHeader\n$rowsSeats\n"
    }

    fun nbSoldTickets(): Int {
        return seats.flatten().count { it == "B" }
    }
}
