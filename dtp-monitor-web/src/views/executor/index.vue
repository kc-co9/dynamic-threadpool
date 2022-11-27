<template>
  <div class="app-container">
    <el-row>

      <el-form
          ref="dataForm"
          inline
          :model="dataForm"
          type="flex"
          justify="center"
      >
        <el-form-item label="线程池ID：" prop="executorId">
          <el-input v-model="dataForm.executorId" placeholder="请输入内容" clearable/>
        </el-form-item>
        <el-form-item label="线程池名字：" prop="executorName">
          <el-input v-model="dataForm.executorName" placeholder="请输入内容" clearable/>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="reset">
            重置
          </el-button>
          <el-button type="primary" @click="search">
            搜索
          </el-button>
        </el-form-item>
      </el-form>

    </el-row>

    <el-row>
      <el-table
          :data="dataTable"
          border
          style="width: 100%">
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" inline class="demo-table-expand">
              <el-form-item label="核心线程数">
                <span>{{ props.row.configBody.corePoolSize }}</span>
              </el-form-item>
              <el-form-item label="最大线程数">
                <span>{{ props.row.configBody.maximumPoolSize }}</span>
              </el-form-item>
              <el-form-item label="最大存活时间">
                <span>{{ props.row.configBody.keepAliveTime }}</span>
              </el-form-item>
              <el-form-item label="拒绝策略">
                <span>{{ props.row.configBody.rejectedStrategy }}</span>
              </el-form-item>
            </el-form>

            <el-form label-position="left" inline class="demo-table-expand">
              <el-form-item label="线程池大小">
                <span>{{ props.row.statisticsBody.poolSize }}</span>
              </el-form-item>
              <el-form-item label="线程池最大线程数量">
                <span>{{ props.row.statisticsBody.largestPoolSize }}</span>
              </el-form-item>
              <el-form-item label="激活任务数量">
                <span>{{ props.row.statisticsBody.activeCount }}</span>
              </el-form-item>
              <el-form-item label="任务数量">
                <span>{{ props.row.statisticsBody.taskCount }}</span>
              </el-form-item>
              <el-form-item label="完成任务数量">
                <span>{{ props.row.statisticsBody.completedTaskCount }}</span>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column
            prop="executorId"
            label="线程池ID">
        </el-table-column>
        <el-table-column
            prop="executorName"
            label="线程池名称">
        </el-table-column>
      </el-table>
    </el-row>
  </div>
</template>


<script>
import {CONSTANTS} from '@/api/common'
import {getExecutorList} from '@/api/executor'

export default {
  name: 'Executor',
  data() {
    return {
      dataForm: {
        serverId: "",
        serverIp: "",
        executorId: "",
        executorName: "",
        pageNo: 1,
        pageSize: 10
      },
      dataTable: [],
      tableLoading: false,
    }
  },
  mounted: function () {
    this.dataForm.serverId = this.$route.query.serverId;
    this.dataForm.serverIp = this.$route.query.serverIp;
    this.pageData();
  },
  methods: {
    search() {
      this.pageData();
    },
    pageChange(e) {
      this.dataForm.pageNo = e
      this.pageData();
    },
    reset() {
      this.dataForm.executorId = "";
      this.dataForm.executorName = "";
    },
    async pageData() {
      this.tableLoading = true
      getExecutorList(this.dataForm)
          .then((response) => {
            if (response.code === CONSTANTS.SUCCESS_CODE) {
              this.dataTable = response.data.executorList
            }
            this.tableLoading = false
          })
          .catch((err) => {
            this.tableLoading = false
          })
    },
  }
}
</script>