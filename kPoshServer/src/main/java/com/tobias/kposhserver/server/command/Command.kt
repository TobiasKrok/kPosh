package com.tobias.kposhserver.server.command
import com.tobias.kposhserver.server.Agent

data class Command (val cmd : String, val agent: Agent, val commandType: CommandType) {

    override fun toString(): String {
        return "CMD:$cmd TYPE:$commandType"
    }
}