package utils

import scala.tools.nsc.Global
import scala.reflect.internal.util.{Position, NoPosition, FakePos}
 
object Reporters {
 
  abstract class ReporterFormatter {
    def formatTypeTitle(typ: MsgType): String
  }
 
  class ConsoleFormatter extends ReporterFormatter {
    def formatTypeTitle(typ: MsgType) = {
      typ match {
        case FatalMsg =>
          Console.RED+Console.BOLD+typ.title+Console.RESET
        case ErrorMsg =>
          Console.RED+typ.title+Console.RESET
        case WarningMsg =>
          Console.YELLOW+typ.title+Console.RESET
        case NormalMsg =>
          Console.MAGENTA+typ.title+Console.RESET
        case InfoMsg => 
          Console.GREEN + typ.title + Console.RESET
        case TitleMsg =>
          Console.BLUE+Console.BOLD+typ.title+Console.RESET
        case DebugMsg =>
          typ.title
      }
    }
  }
 
  class PlainFormatter extends ReporterFormatter {
    def formatTypeTitle(typ: MsgType) = {
      typ.title
    }
  }
 
  sealed abstract class MsgType {
    val title: String
  }
 
  object MsgType {
    val maxTitleSize = WarningMsg.title.size
  }
 
  case object TitleMsg extends MsgType {
    val title = "title"
  }
 
  case object FatalMsg extends MsgType {
    val title = "fatal"
  }
 
  case object ErrorMsg extends MsgType {
    val title = "error"
  }
 
  case object NormalMsg extends MsgType {
    val title = "msg"
  }
 
  case object InfoMsg extends MsgType {
    val title = "info"
  }
 
  case object WarningMsg extends MsgType {
    val title = "warning"
  }
 
  case object DebugMsg extends MsgType {
    val title = "debug"
  }
 
  final case class MsgLines(lines: Seq[String]);
 
  case class Msg(lines: Seq[String], typ: MsgType) {
    def content = lines.mkString("\n")
 
    val firstLine  = lines.head
    val otherLines = lines.tail
  }
 
  import language.implicitConversions
 
  implicit def posToOptPos(p: Position): Option[Position] = Some(p)
  implicit def strToMsgLines(m: String): MsgLines         = MsgLines(Seq(m))
  implicit def seqStrToMsgLines(m: Seq[String]): MsgLines = MsgLines(m)
 
 
  trait ReporterHandler {
    def open() { }
    def incIndent() { }
    def decIndent() { }
    def close() { }
 
    def printMessage(msg: Msg, optPos: Option[Position])
    def printText(content: String)
  }
 
  class ConsoleReporterHandler() extends ReporterHandler {
    var currentIndent: Int = 0;
    val indentStep         = 8;
 
    val formatter = new ConsoleFormatter
 
    protected def posToString(optPos: Option[Position]): String = {
      optPos match {
          case Some(posIn) =>
            val pos = if (posIn eq null) NoPosition
                 else if (posIn.isDefined) posIn.inUltimateSource(posIn.source)
                 else posIn
 
            pos match {
              case FakePos(fmsg) =>
                "?:? ("+fmsg+"): "
              case NoPosition =>
                ""
 
              case _ =>
                val file = pos.source.file
 
                file.path+":"+pos.line+": "
            }
 
           case None =>
            ""
      }
    }
 
    override def incIndent() {
      currentIndent += indentStep
    }
    override def decIndent() {
      currentIndent -= indentStep
    }
 
    override def printMessage(msg: Msg, optPos: Option[Position]) {
      val strPos = posToString(optPos)
 
      val indent  = " "*currentIndent
      val padding = " "*(MsgType.maxTitleSize-msg.typ.title.size)
 
      printText(formatter.formatTypeTitle(msg.typ)+padding+": "+indent+msg.firstLine+"\n")
      for (line <- msg.otherLines) {
        printText(" "*(MsgType.maxTitleSize+(": "+indent).length) + line+"\n")
      }
 
      optPos match {
          case Some(posIn) if posIn ne null=>
            val pos = if (posIn.isDefined) posIn.inUltimateSource(posIn.source)
                      else posIn
 
            pos match {
              case FakePos(fmsg) =>
              case NoPosition =>
              case _ =>
                printSourceLine(strPos, pos)
            }
          case _ =>
      }
    }
 
    def printSourceLine(prefix: String, pos: Position) = {
      printText(prefix+pos.lineContent.stripLineEnd+"\n")
      if (pos.isDefined) {
        printText((" " * (pos.column - 1 + prefix.length) + "^\n"))
      }
    }
 
    def printText(content: String) {
      print(content)
    }
 
  }
 
  class Reporter() {
    var handlers = Set[ReporterHandler]()
 
    def dispatch(cb: ReporterHandler => Unit) {
      handlers.foreach(cb)
    }
 
    def attach(rh: ReporterHandler) {
      handlers += rh
    }
 
    def detach(rh: ReporterHandler) {
      handlers -= rh
    }
 
    def open() {
      dispatch(_.open)
    }
 
    def close() {
      dispatch(_.close)
    }
 
    def printMessage(m: Msg, optPos: Option[Position]) {
      dispatch { rh => rh.printMessage(m, optPos) }
    }
 
    def printText(s: String) {
      dispatch { rh => rh.printText(s) }
    }
 
    def incIndent() {
      dispatch(_.incIndent)
    }
 
    def decIndent() {
      dispatch(_.decIndent)
    }
 
    def msg(m: MsgLines,   optPos: Option[Position] = None) =
      printMessage(Msg(m.lines, NormalMsg), optPos)
 
    def info(m: MsgLines,  optPos: Option[Position] = None) =
      printMessage(Msg(m.lines, InfoMsg), optPos)
 
    def error(m: MsgLines, optPos: Option[Position] = None) =
      printMessage(Msg(m.lines, ErrorMsg), optPos)
 
    def fatal(m: MsgLines, optPos: Option[Position] = None) = {
      printMessage(Msg(m.lines, FatalMsg), optPos)
      sys.error("Panic! Evacuate Ship!")
    }
 
    def debug(m: MsgLines, optPos: Option[Position] = None) =
      printMessage(Msg(m.lines, DebugMsg), optPos)
 
    def warn(m: MsgLines,  optPos: Option[Position] = None) =
      printMessage(Msg(m.lines, WarningMsg), optPos)
 
    def title(m: String) {
      printMessage(Msg(Seq(m), TitleMsg), None)
    }
  }
 
 
  case class CompilerReporterPassThrough(as: (String, Position) => Unit) extends scala.tools.nsc.reporters.Reporter {
    protected def info0(pos: Position, msg: String, severity: Severity, force: Boolean) {
      as(msg, pos)
    }
  }
}
