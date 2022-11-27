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
        <el-form-item label="服务ID：" prop="serverId">
          <el-input v-model="dataForm.serverId" placeholder="请输入内容" clearable/>
        </el-form-item>
        <el-form-item label="服务代码：" prop="serverCode">
          <el-input v-model="dataForm.serverCode" placeholder="请输入内容" clearable/>
        </el-form-item>
        <el-form-item label="服务名称：" prop="serverName">
          <el-input v-model="dataForm.serverName" placeholder="请输入内容" clearable/>
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
      <h4 style="margin:0">
        共搜到{{ dataTable.total }}条数据
      </h4>
      <el-table
          :data="dataTable.records"
          border
          style="width: 100%">
        <el-table-column type="expand">
          <template slot-scope="scope">
            <el-table
                :data="scope.row.serverIpList"
                border
                style="width: 100%">
              <el-table-column align="center" label="服务正在运行IP">
                <template slot-scope="subScope">
                  {{ subScope.row }}
                </template>
              </el-table-column>
              <el-table-column align="center" label="操作" fixed="right">
                <template slot-scope="subScope">
                  <el-link type="primary" @click="gotoExecutorDetail(scope.row.serverId,subScope.row)">
                    查看详情
                  </el-link>
                </template>
              </el-table-column>
            </el-table>
          </template>
        </el-table-column>
        <el-table-column
            prop="serverId"
            label="服务ID">
        </el-table-column>
        <el-table-column
            prop="serverCode"
            label="服务代码">
        </el-table-column>
        <el-table-column
            prop="serverName"
            label="服务名称">
        </el-table-column>
        <el-table-column
            prop="serverSecret"
            label="服务密钥">
        </el-table-column>
        <el-table-column
            prop="isRunning"
            label="服务是否正在运行">
          <template slot-scope="scope">
            {{ scope.row.isRunning ? '运行中' : '下线' }}
          </template>
        </el-table-column>
      </el-table>
    </el-row>
    <el-row type="flex" justify="center" style="margin-top:20px">
      <el-pagination
          :current-page="parseInt(dataTable.current)"
          :total="parseInt(dataTable.total)"
          layout="sizes,total, prev, pager, next, jumper"
          @current-change="pageChange"
      />
    </el-row>
  </div>
</template>


<script>
import {CONSTANTS} from '@/api/common'
import {getServerList} from '@/api/server'

export default {
  name: 'Server',
  data() {
    return {
      dataForm: {
        serverId: "",
        serverCode: "",
        serverName: "",
        pageNo: 1,
        pageSize: 10
      },
      dataTable: {
        current: 1,
        size: 10,
        total: 0,
        pages: 0,
        records: []
      },
      tableLoading: false,
    }
  },
  mounted: function () {
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
      this.dataForm.serverId = "";
      this.dataForm.serverCode = "";
      this.dataForm.serverName = "";
    },
    async pageData() {
      this.tableLoading = true
      getServerList(this.dataForm)
          .then((response) => {
            if (response.code === CONSTANTS.SUCCESS_CODE) {
              this.dataTable = response.data.serverList
            }
            this.tableLoading = false
          })
          .catch((err) => {
            this.tableLoading = false
          })
    },
    gotoExecutorDetail(serverId, serverIp) {
      let data = {name: 'Executor'}
      data = {
        ...data, query: {
          serverId: serverId,
          serverIp: serverIp
        }
      }
      this.$router.push(data)
    },
  }
}
</script>