package io.joern.scanners.c

import io.joern.console.*
import io.joern.dataflowengineoss.language.*
import io.joern.dataflowengineoss.queryengine.EngineContext
import io.joern.macros.QueryMacros.*
import io.joern.scanners.{Crew, QueryTags}
import io.shiftleft.codepropertygraph.generated.nodes.*
import io.shiftleft.semanticcpg.language.*

object UseAfterFree extends QueryBundle {

  implicit val resolver: ICallResolver = NoResolve

  @q
  def freePostDominatesUsage(): Query =
    Query.make(
      name = "free-follows-value-reuse-demo",
      author = Crew.malte,
      title = "A value that is free'd is reused without reassignment.",
      description = """
        |A value is used after being free'd in a path that leads to it
        |without reassignment.
        |This demo is adapted from Joern's official UseAfterFree query.
        |""".stripMargin,
      score = 5.0,
      withStrRep({ cpg =>
        cpg.method
          .name("(.*_)?free")
          .filter(_.parameter.size == 1)
          .callIn
          .where(_.argument(1).isIdentifier)
          .flatMap { f =>
            val freedIdentifierCode = f.argument(1).code
            val postDom             = f.postDominatedBy.toSetImmutable

            val assignedPostDom = postDom.isIdentifier
              .where(_.inAssignment)
              .codeExact(freedIdentifierCode)
              .flatMap(id => Iterator.single(id) ++ id.postDominatedBy)

            postDom
              .removedAll(assignedPostDom)
              .isIdentifier
              .codeExact(freedIdentifierCode)
          }
      }),
      tags = List(QueryTags.uaf),
      codeExamples = CodeExamples(
        List("""
          |
          |void *bad() {
          |  void *x = NULL;
          |  free(x);
          |  return x;
          |}
          |
          |""".stripMargin),
        List("""
          |
          |void *good() {
          |  void *x = NULL;
          |  free(x);
          |  x = NULL;
          |  return x;
          |}
          |
          |""".stripMargin)
      )
    )
}