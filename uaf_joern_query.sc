import io.shiftleft.semanticcpg.language.*

val findings = cpg.method
  .name("(.*_)?free")
  .filter(_.parameter.size == 1)
  .callIn
  .where(_.argument(1).isIdentifier)
  .flatMap { f =>
    val freedIdentifierCode = f.argument(1).code
    val postDom = f.postDominatedBy.toSetImmutable

    val assignedPostDom = postDom.isIdentifier
      .where(_.inAssignment)
      .codeExact(freedIdentifierCode)
      .flatMap(id => Iterator.single(id) ++ id.postDominatedBy)

    postDom
      .removedAll(assignedPostDom)
      .isIdentifier
      .codeExact(freedIdentifierCode)
      .map(id => (f.method.name, f.code, id.code, id.lineNumber.getOrElse(-1)))
  }
  .l

findings.foreach { case (func, freeCall, laterUse, line) =>
  println("Possible UAF")
  println("Function: " + func)
  println("free call: " + freeCall)
  println("later use: " + laterUse)
  println("line: " + line)
  println("---")
}