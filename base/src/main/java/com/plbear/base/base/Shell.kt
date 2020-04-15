package com.plbear.base.base

/**
 * created by yanyongjun on 2020-04-15
 */
import android.text.TextUtils
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader

class Shell private constructor() {

    init {
        throw AssertionError()
    }

    /**
     * result of command
     *
     *  * [CommandResult.result] means result of command, 0 means normal, else means error, same to excute in
     * linux shell
     *  * [CommandResult.successMsg] means success message of command result
     *  * [CommandResult.errorMsg] means error message of command result
     *
     *
     * @author [Trinea](http://www.trinea.cn) 2013-5-16
     */
    class CommandResult {

        /**
         * result of command
         */
        var result: Int = 0

        /**
         * success message of command result
         */
        var successMsg: String = ""

        /**
         * error message of command result
         */
        var errorMsg: String = ""

        constructor(result: Int) {
            this.result = result
        }

        constructor(result: Int, successMsg: String, errorMsg: String) {
            this.result = result
            this.successMsg = successMsg
            this.errorMsg = errorMsg
        }

        override fun toString(): String {
            return "CommandResult{" +
                    "result=" + result +
                    ", successMsg='" + successMsg + '\''.toString() +
                    ", errorMsg='" + errorMsg + '\''.toString() +
                    '}'.toString()
        }
    }

    companion object {

        private val COMMAND_SU = "su"
        private val COMMAND_SH = "sh"
        private val COMMAND_EXIT = "exit\n"
        private val COMMAND_LINE_END = "\n"

        /**
         * check whether has root permission
         *
         * @return
         */
        fun checkRootPermission(): Boolean {
            return execCommand("echo root", true, false).result == 0
        }

        /**
         * execute shell command, default return result msg
         *
         * @param command command
         * @param isRoot  whether need to run with root
         * @return
         * @see ShellUtils.execCommand
         */
        fun execCommand(command: String, isRoot: Boolean): CommandResult {
            return execCommand(arrayOf(command), isRoot, true)
        }

        /**
         * execute shell commands, default return result msg
         *
         * @param commands command list
         * @param isRoot   whether need to run with root
         * @return
         * @see ShellUtils.execCommand
         */
        fun execCommand(commands: List<String>?, isRoot: Boolean): CommandResult {
            return execCommand(commands?.toTypedArray(), isRoot, true)
        }

        /**
         * execute shell command
         *
         * @param command         command
         * @param isRoot          whether need to run with root
         * @param isNeedResultMsg whether need result msg
         * @return
         * @see ShellUtils.execCommand
         */
        fun execCommand(command: String, isRoot: Boolean, isNeedResultMsg: Boolean): CommandResult {
            return execCommand(arrayOf(command), isRoot, isNeedResultMsg)
        }

        /**
         * execute shell commands
         *
         * @param commands        command list
         * @param isRoot          whether need to run with root
         * @param isNeedResultMsg whether need result msg
         * @return
         * @see ShellUtils.execCommand
         */
        fun execCommand(commands: List<String>?, isRoot: Boolean, isNeedResultMsg: Boolean): CommandResult {
            return execCommand(commands?.toTypedArray(), isRoot, isNeedResultMsg)
        }

        /**
         * execute shell commands
         *
         * @param commands        command array
         * @param isRoot          whether need to run with root
         * @param isNeedResultMsg whether need result msg
         * @return
         *  * if isNeedResultMsg is false, [CommandResult.successMsg] is null and
         * [CommandResult.errorMsg] is null.
         *  * if [CommandResult.result] is -1, there maybe some excepiton.
         *
         */
        @JvmOverloads
        fun execCommand(commands: Array<String>?, isRoot: Boolean, isNeedResultMsg: Boolean = true): CommandResult {
            var result = -1
            if (commands == null || commands.size == 0) {
                return CommandResult(result, "", "")
            }

            var process: Process? = null
            var successResult: BufferedReader? = null
            var errorResult: BufferedReader? = null
            var successMsg = StringBuilder()
            var errorMsg = StringBuilder()

            var os: DataOutputStream? = null
            try {
                process = Runtime.getRuntime().exec(if (isRoot) COMMAND_SU else COMMAND_SH)
                os = DataOutputStream(process!!.outputStream)
                for (command in commands) {
                    if (TextUtils.isEmpty(command)) {
                        continue
                    }
                    // donnot use os.writeBytes(commmand), avoid chinese charset error
                    os.write(command.toByteArray())
                    os.writeBytes(COMMAND_LINE_END)
                    os.flush()
                }
                os.writeBytes(COMMAND_EXIT)
                os.flush()

                result = process.waitFor()
                // get command result
                if (isNeedResultMsg) {
                    successResult = BufferedReader(InputStreamReader(process.inputStream))
                    errorResult = BufferedReader(InputStreamReader(process.errorStream))
                    var s: String? = successResult.readLine()
                    while (s != null) {
                        successMsg.append(s)
                        s = successResult.readLine()
                    }
                    s = errorResult.readLine()
                    while (s != null) {
                        errorMsg.append(s)
                        s = errorResult.readLine()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    os?.close()
                    successResult?.close()
                    errorResult?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                process?.destroy()
            }
            return CommandResult(result, successMsg.toString(), errorMsg.toString())
        }

        fun startPayByWeb() {
            execCommand("am instrument -w -r   -e debug false -e class 'com.mahuateng.testrunner.alipay.AlipayTest#alipay' com.mahuateng.testrunner.test/android.support.test.runner.AndroidJUnitRunner", true)
        }
    }
}