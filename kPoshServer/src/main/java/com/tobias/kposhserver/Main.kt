package com.tobias.kposhserver

import com.tobias.kposhserver.commandline.KPoshCli
import com.tobias.kposhserver.server.Server
import com.tobias.kposhserver.server.worker.AgentWorker
import org.fusesource.jansi.AnsiConsole
import org.jline.builtins.Builtins
import org.jline.builtins.Completers
import org.jline.builtins.Options
import org.jline.reader.*
import org.jline.reader.impl.DefaultParser
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle
import picocli.CommandLine
import picocli.shell.jline3.PicocliCommands
import java.nio.file.Path
import java.nio.file.Paths

fun main(args: Array<String>) {
    val server : Server = Server(420)
    Thread(server).start()
    val worker = server.worker
    startCli(worker,server)
    }

public fun startCli(worker: AgentWorker, server: Server) {
    AnsiConsole.systemInstall();
    try { // set up JLine built-in commands
        val workDir: Path = Paths.get("")
        val builtins = Builtins(workDir, null, null)
        val systemCompleter: Completers.SystemCompleter = builtins.compileCompleters()
        // set up picocli commands
        // Pass server and worker object so commandline can interact
        val commands = KPoshCli(server,worker)
        val cmd = CommandLine(commands)
        val picocliCommands = PicocliCommands(workDir, cmd)
        systemCompleter.add(picocliCommands.compileCompleters())
        systemCompleter.compile()
        val terminal: Terminal = TerminalBuilder.builder().build()
        val reader: LineReader = LineReaderBuilder.builder()
            .terminal(terminal)
            .completer(systemCompleter)
            .parser(DefaultParser())
            .variable(LineReader.LIST_MAX, 50) // max tab completion candidates
            .build()
        builtins.setLineReader(reader)
        commands.setReader(reader)
        val prompt = "$>"
        val rightPrompt: String? = null
        // start the shell and process input until the user quits with Ctl-D
        var line: String
        while (true) {
            try {
                line = reader.readLine(prompt, rightPrompt, null as MaskingCallback?, null)
                if (line.matches(Regex("^\\s*#.*"))) {
                    continue
                }
                val pl: ParsedLine = reader.parser.parse(line, 0)
                val arguments: Array<String> = pl.words().toTypedArray()

                val command: String = Parser.getCommand(pl.word())
                if (builtins.hasCommand(command)) {
                    builtins.execute(
                        command, arguments.copyOfRange(1, arguments.size)
                        , System.`in`, System.out, System.err
                    )
                } else {
                    CommandLine(commands).execute(*arguments)
                }
            } catch (e: Options.HelpException) {
                Options.HelpException.highlight(e.message, Options.HelpException.defaultStyle()).print(terminal)
            } catch (e: UserInterruptException) { // Ignore
            } catch (e: EndOfFileException) {
                return
            } catch (e: Exception) {
                val asb = AttributedStringBuilder()
                asb.append(e.message, AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
                asb.toAttributedString().println(terminal)
            }
        }
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}
