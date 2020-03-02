package com.tobias.kposhserver.commandline

import com.tobias.kposhserver.server.Agent
import com.tobias.kposhserver.server.Server
import com.tobias.kposhserver.server.command.Command
import com.tobias.kposhserver.server.command.CommandType
import com.tobias.kposhserver.server.worker.AgentWorker
import org.fusesource.jansi.AnsiConsole
import org.jline.builtins.Builtins
import org.jline.builtins.Completers
import org.jline.builtins.Options
import org.jline.reader.*
import org.jline.reader.impl.DefaultParser
import org.jline.reader.impl.LineReaderImpl
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle
import picocli.CommandLine
import picocli.shell.jline3.PicocliCommands
import java.io.PrintWriter
import java.nio.file.Path
import java.nio.file.Paths


@CommandLine.Command(
        name = "",
        description = ["KPosh CLI"],
        subcommands = [com.tobias.kposhserver.commandline.commands.agent.Agent::class, com.tobias.kposhserver.commandline.commands.session.Session::class])

class KPoshCli(private val server: Server, private val agentWorker: AgentWorker) : Runnable {
    private lateinit var reader: LineReaderImpl
    lateinit var out: PrintWriter
    private var inSession = false
    private lateinit var selectedAgents: ArrayList<Agent>

    private fun setReader(reader: LineReader) {
        this.reader = reader as LineReaderImpl
        this.out = reader.terminal.writer()
    }

    override fun run() {
        out.println(CommandLine(this).usageMessage)
    }

    // Return agent map
    fun agents(): HashMap<Int, Agent> = server.agents

    // If we are in session, all commands entered will be passed to agentWorker to be sent to the remote agent.
    fun inSession(inSession: Boolean, agents: List<Agent>) {
        this.inSession = inSession
        if (this.inSession) {
            selectedAgents = agents as ArrayList<Agent>
        } else {
            selectedAgents.clear()
        }

    }

    public fun startCli() {
        AnsiConsole.systemInstall();
        try { // set up JLine built-in commands
            val workDir: Path = Paths.get("")
            val builtins = Builtins(workDir, null, null)
            val systemCompleter: Completers.SystemCompleter = builtins.compileCompleters()
            // set up picocli commands
            // Pass server and worker object so commandline can interact
            val commands = this
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
            var prompt = "$>"
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
                    if (inSession) {
                        prompt = "AGENT[${selectedAgents.map { it.id }.joinToString(",")}]>"
                        for (agent: Agent in selectedAgents) {
                            agentWorker.process(Command(arguments.joinToString(" "), agent,CommandType.CALL_AGENT))
                        }
                    } else if (builtins.hasCommand(command)) {
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
}
