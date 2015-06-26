package cafesat
package it

import scala.sys.process._

import org.scalatest.FunSuite
import org.scalatest.Matchers

import java.io.File
import java.io.FileReader

import parsers.Dimacs

class Tests extends FunSuite with Matchers {

  val all: String => Boolean = (s: String) => true
  val resourceDirHard = "src/it/resources/"

  def filesInResourceDir(dir : String, filter : String=>Boolean = all) : Iterable[File] = {    
    import scala.collection.JavaConversions._
    val d = this.getClass.getClassLoader.getResource(dir)
    val asFile = if(d == null || d.getProtocol != "file") {
      // We are in Eclipse. The only way we are saved is by hard-coding the path               
      new File(resourceDirHard + dir)
    } else new File(d.toURI())
    asFile.listFiles().filter(f => filter(f.getPath()))
  }

//  def mkTest(file: File)(block: => Unit) = {
//
//    if(isZ3Available) {
//      test("SMTLIB benchmark: " + file.getPath) {
//        (new ScriptRunner).run(file)
//      }
//    }
//
//    if(isCVC4Available) {
//
//    }
//
//  }
//

  filesInResourceDir("regression/dimacs/sat", _.endsWith(".cnf")).foreach(file => {
    test("Checking SAT solver on sat instance: " + file.getPath) {
      val res = runSatSolver(file)
      res shouldBe a [sat.Solver.Results.Satisfiable]
    }
  })

  filesInResourceDir("regression/dimacs/unsat", _.endsWith(".cnf")).foreach(file => {
    test("Checking SAT solver on unsat instance: " + file.getPath) {
      val res = runSatSolver(file)
      res shouldBe sat.Solver.Results.Unsatisfiable
    }
  })


  def runSatSolver(file: File): sat.Solver.Results.Result = {
    import sat._

    val (satInstance, nbVars) = Dimacs.cnf(new FileReader(file))
    val s = new Solver(nbVars)

    satInstance.foreach(s.addClause(_))
    val result = s.solve()
    result
  }


  //TODO: check incrementally the solver
  //  var i = 0
  //  var sResult: Result = Unknown
  //  for(c <- satInstance) {
  //    s.addClause(c)
  //    sResult = s.solve()
  //    i += 1

  //    // reference solver (all clauses added immediately)
  //    val r = new Solver(nbVars)
  //    satInstance.take(i).foreach(r.addClause(_))
  //    val rResult = r.solve()
  //    assert(sResult.getClass === rResult.getClass)
  //  }

  //  assert(sResult === Unsatisfiable)
  //}


}