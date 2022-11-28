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
            <el-table
                :data="[props.row.executorConfig]"
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
                  <el-button type="text" @click="showConfigure(props.row.executorId, scope.row)">
                    配置
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
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
        <el-table-column
            align="center"
            label="操作"
            fixed="right"
            width="120">
          <template slot-scope="scope">
            <el-button type="text" @click="gotoExecutorDetail(scope.row)">
              详情
            </el-button>
          </template>
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
import {configureExecutor, getExecutorList} from '@/api/executor'

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
    this.configureForm.serverId = this.$route.query.serverId;
    this.configureForm.serverIp = this.$route.query.serverIp;
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
    showConfigure(executorId, row) {
      this.configureVisible = true
      this.configureForm.executorId = executorId;
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
    gotoExecutorDetail(row) {
      let data = {name: 'ExecutorDetail'}
      data = {
        ...data, query: {
          serverId: this.dataForm.serverId,
          serverIp: this.dataForm.serverIp,
          executorId: row.executorId,
        }
      }
      this.$router.push(data)
    }
  }
}
</script>