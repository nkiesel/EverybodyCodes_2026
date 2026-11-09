import kotlin.io.path.Path
import kotlin.io.path.readLines

fun lines(quest: String, part: Int) = Path("input/everybody_codes_e2026_q${quest}_p${part}.txt").readLines()
