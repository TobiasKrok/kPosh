package com.tobias.kposhserver.commandline

import com.tobias.kposhserver.server.Agent
import com.tobias.kposhserver.server.Server
import com.tobias.kposhserver.server.worker.AgentWorker
import org.fusesource.jansi.AnsiConsole
import org.jline.builtins.Builtins
import org.jline.builtins.Completers
import org.jline.builtins.Options
import org.jline.builtins.Widgets.TailTipWidgets
import org.jline.builtins.Widgets.TailTipWidgets.TipType
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
import java.security.Key
import java.util.*
import kotlin.collections.HashMap


@CommandLine.Command (name = "",description = ["KPosh CLI", "Subcommands: agent"] ,subcommands = [com.tobias.kposhserver.commandline.commands.Agent::class])

class KPoshCli(private val server : Server, private val agentWorker : AgentWorker) : Runnable {
    lateinit var reader: LineReaderImpl
    lateinit var out: PrintWriter

    fun setReader(reader: LineReader) {
        this.reader = reader as LineReaderImpl
        this.out = reader.terminal.writer()
    }

    override fun run() {
        out.println(CommandLine(this).usageMessage)
    }
    // Return agent map
    fun agents() : HashMap<Int, Agent> = server.agents
}
