import io.joern.console.*
import io.shiftleft.semanticcpg.language.*

@main def main() = {
  importCpg("uaf_cpg.bin")

  def loadedSlotBefore(calls: List[io.shiftleft.codepropertygraph.generated.nodes.Call], idx: Int): String = {
    calls.take(idx).reverse
      .find(c => c.code.startsWith("ldr x0,") || c.code.startsWith("ldur x0,"))
      .flatMap(_.argument.code.l.lastOption)
      .getOrElse("")
  }

  cpg.method.name(".*bad_uaf.*|.*good_different_pointer.*|.*good_stack_pointer.*").foreach { m =>
    val calls = m.call.l

    calls.zipWithIndex.foreach { case (f, i) =>
      if (f.name == "_free") {
        val freedSlot = loadedSlotBefore(calls, i)

        calls.zipWithIndex.drop(i + 1).foreach { case (s, j) =>
          if (s.name == "_sink") {
            val sinkSlot = loadedSlotBefore(calls, j)

            if (freedSlot.nonEmpty && freedSlot == sinkSlot) {
              println("Possible Use-After-Free detected:")
              println("Function: " + m.name)
              println("Freed memory slot: " + freedSlot)
              println("Free call: " + f.code)
              println("Later sink call: " + s.code)
              println("---")
            }
          }
        }
      }
    }
  }
}

