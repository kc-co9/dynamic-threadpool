<template>
  <div class="app-container">
    <el-row>
      <h4>线程池配置信息</h4>
      <el-table
          :data="[dataResult.executorConfig]"
          border
          style="width: 100%">
        <el-table-column align="center" property="corePoolSize" label="核心线程数"></el-table-column>
        <el-table-column align="center" property="maximumPoolSize" label="最大线程数"></el-table-column>
        <el-table-column align="center" property="keepAliveTime" label="最大存活时间"></el-table-column>
        <el-table-column align="center" property="rejectedStrategy" label="拒绝策略"></el-table-column>
        <el-table-column
            align="center"
            label="操作"
            fixed="right"
            width="120">
          <template slot-scope="scope">
            <el-button type="text" @click="showConfigure(scope.row)">
              配置
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-row>

    <el-row>
      <h4>线程池统计信息</h4>
      <el-table
          :data="[dataResult.executorStatistics]"
          border
          style="width: 100%">
        <el-table-column
            prop="queueClass"
            label="队列类型">
        </el-table-column>
        <el-table-column
            prop="queueNodeCount"
            label="队列节点数">
        </el-table-column>
        <el-table-column
            prop="queueRemainingCapacity"
            label="队列剩余容量">
        </el-table-column>
        <el-table-column
            prop="poolSize"
            label="当前线程数">
        </el-table-column>
        <el-table-column
            prop="largestPoolSize"
            label="最大线程数（历史）">
        </el-table-column>
        <el-table-column
            prop="activeCount"
            label="执行中线程数">
        </el-table-column>
        <el-table-column
            prop="taskCount"
            label="已调度任务数">
        </el-table-column>
        <el-table-column
            prop="completedTaskCount"
            label="已完成任务数">
        </el-table-column>
      </el-table>
    </el-row>

    <el-dialog title="配置线程池" :visible.sync="configureVisible">
      <el-form :model="configureForm" label-position="right" label-width="120px">
        <el-form-item label="核心线程数">
          <el-input v-model="configureForm.executorConfig.corePoolSize" placeHolder="请输入" style="width: 300px"/>
        </el-form-item>
        <el-form-item label="最大线程数">
          <el-input v-model="configureForm.executorConfig.maximumPoolSize" placeHolder="请输入" style="width: 300px"/>
        </el-form-item>
        <el-form-item label="最大存活时间">
          <el-input v-model="configureForm.executorConfig.keepAliveTime" placeHolder="请输入" style="width: 300px"/>
        </el-form-item>
        <el-form-item label="拒绝策略">
          <el-input v-model="configureForm.executorConfig.rejectedStrategy" placeHolder="请输入" style="width: 300px"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="configureVisible = false">
          取 消
        </el-button>
        <el-button type="primary" @click="configure">
          确 定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>


<script>
import {CONSTANTS} from '@/api/common'
import {configureExecutor, getExecutorDetail, getExecutorList} from '@/api/executor'

export default {
  name: 'ExecutorDetail',
  data() {
    return {
      dataForm: {
        serverId: "",
        serverIp: "",
        executorId: "",
      },
      dataResult: {
        executorConfig: null,
        executorStatistics: null
      },
      tableLoading: false,
      configureVisible: false,
      configureForm: {
        serverId: null,
        serverIp: null,
        executorId: null,
        executorConfig: {
          executorId: null,
          executorName: null,
          corePoolSize: null,
          maximumPoolSize: null,
          keepAliveTime: null,
          rejectedStrategy: null
        }
      }
    }
  },
  mounted: function () {
    this.dataForm.serverId = this.$route.query.serverId;
    this.dataForm.serverIp = this.$route.query.serverIp;
    this.dataForm.executorId = this.$route.query.executorId;

    this.configureForm.serverId = this.$route.query.serverId;
    this.configureForm.serverIp = this.$route.query.serverIp;
    this.configureForm.executorId = this.$route.query.executorId;

    this.getDetail();
  },
  methods: {
    async getDetail() {
      this.tableLoading = true
      getExecutorDetail(this.dataForm)
          .then((response) => {
            if (response.code === CONSTANTS.SUCCESS_CODE) {
              this.dataResult = response.data
            }
            this.tableLoading = false
          })
          .catch((err) => {
            this.tableLoading = false
          })
    },
    showConfigure(row) {
      this.configureVisible = true
      this.configureForm.executorConfig = {
        executorId: row.executorId,
        executorName: row.executorName,
        corePoolSize: row.corePoolSize,
        maximumPoolSize: row.maximumPoolSize,
        keepAliveTime: row.keepAliveTime,
        rejectedStrategy: row.rejectedStrategy,
      }
    },
    configure(row) {
      configureExecutor(this.configureForm)
          .then((response) => {
            if (response.code === CONSTANTS.SUCCESS_CODE) {
              this.configureVisible = false
            }
          })
          .catch((err) => {
          })
    },
  }
}
</script>