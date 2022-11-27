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
          <el-button type="primary" @click="showInsert">
            新增
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
        <el-table-column
            align="center"
            label="操作"
            fixed="right"
            width="120">
          <template slot-scope="scope">
            <el-button type="text" @click="showEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="text" @click="remove(scope.row)">
              删除
            </el-button>
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

    <el-dialog title="新增服务" :visible.sync="insertVisible">
      <el-form :model="insertForm" label-position="right" label-width="120px">
        <el-form-item label="服务代码">
          <el-input v-model="insertForm.serverCode" placeHolder="请输入" style="width: 300px"/>
        </el-form-item>
        <el-form-item label="服务名称">
          <el-input v-model="insertForm.serverName" placeHolder="请输入" style="width: 300px"/>
        </el-form-item>
        <el-form-item label="服务密钥">
          <el-input v-model="insertForm.serverSecret" placeHolder="请输入" style="width: 300px"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="insertVisible = false">
          取 消
        </el-button>
        <el-button type="primary" @click="insert">
          确 定
        </el-button>
      </div>
    </el-dialog>

    <el-dialog title="编辑服务" :visible.sync="editVisible">
      <el-form :model="editForm" label-position="right" label-width="120px">
        <el-form-item label="服务代码">
          <el-input v-model="editForm.serverCode" placeHolder="请输入" style="width: 300px"/>
        </el-form-item>
        <el-form-item label="服务名称">
          <el-input v-model="editForm.serverName" placeHolder="请输入" style="width: 300px"/>
        </el-form-item>
        <el-form-item label="服务密钥">
          <el-input v-model="editForm.serverSecret" placeHolder="请输入" style="width: 300px"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editVisible = false">
          取 消
        </el-button>
        <el-button type="primary" @click="edit">
          确 定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>


<script>
import {CONSTANTS} from '@/api/common'
import {getServerList, insertServer, updateServer, deleteServer} from '@/api/server'

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
      insertVisible: false,
      insertForm: {
        serverCode: null,
        serverName: null,
        serverSecret: null
      },
      editVisible: false,
      editForm: {
        serverId: null,
        serverCode: null,
        serverName: null,
        serverSecret: null
      }
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
    showInsert() {
      this.insertForm.serverCode = null
      this.insertForm.serverName = null
      this.insertForm.serverSecret = null
      this.insertVisible = true;
    },
    insert() {
      insertServer(this.insertForm)
          .then((response) => {
            if (response.code === CONSTANTS.SUCCESS_CODE) {
              this.search();
              this.insertVisible = false
              this.insertForm.serverCode = null
              this.insertForm.serverName = null
              this.insertForm.serverSecret = null
            }
          })
          .catch((err) => {
          })
    },
    showEdit(row) {
      this.editForm.serverId = row.serverId
      this.editForm.serverCode = row.serverCode
      this.editForm.serverName = row.serverName
      this.editForm.serverSecret = row.serverSecret
      this.editVisible = true
    },
    edit() {
      updateServer(this.editForm)
          .then((response) => {
            if (response.code === CONSTANTS.SUCCESS_CODE) {
              this.search();
              this.editVisible = false
              this.editForm.serverId = null
              this.editForm.serverCode = null
              this.editForm.serverName = null
              this.editForm.serverSecret = null
            }
          })
          .catch((err) => {
          })
    },
    remove(row) {
      deleteServer({serverId: row.serverId})
          .then((response) => {
            if (response.code === CONSTANTS.SUCCESS_CODE) {
              this.search();
            }
          })
          .catch((err) => {
          })
    }
  }
}
</script>