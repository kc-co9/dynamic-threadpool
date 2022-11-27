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
        <el-form-item label="账号：" prop="account">
          <el-input v-model="dataForm.account" placeholder="请输入内容" clearable/>
        </el-form-item>
        <el-form-item label="用户名：" prop="username">
          <el-input v-model="dataForm.username" placeholder="请输入内容" clearable/>
        </el-form-item>
        <el-form-item label="邮箱：" prop="email">
          <el-input v-model="dataForm.email" placeholder="请输入内容" clearable/>
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
        <el-table-column
            prop="id"
            label="ID">
        </el-table-column>
        <el-table-column
            prop="account"
            label="账号">
        </el-table-column>
        <el-table-column
            prop="username"
            label="用户名">
        </el-table-column>
        <el-table-column
            prop="email"
            label="邮箱">
        </el-table-column>
        <el-table-column
            prop="createTime"
            label="创建时间">
        </el-table-column>
        <el-table-column
            prop="updateTime"
            label="更新时间">
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

    <el-dialog title="新增管理员" :visible.sync="insertVisible">
      <el-form :model="insertForm" label-position="right" label-width="120px">
        <el-form-item label="账号">
          <el-input v-model="insertForm.account" placeHolder="请输入" style="width: 300px"/>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="insertForm.username" placeHolder="请输入" style="width: 300px"/>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="insertForm.email" placeHolder="请输入" style="width: 300px"/>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="insertForm.password" placeHolder="请输入" style="width: 300px"/>
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

    <el-dialog title="编辑管理员" :visible.sync="editVisible">
      <el-form :model="editForm" label-position="right" label-width="120px">
        <el-form-item label="账号">
          <el-input v-model="editForm.account" placeHolder="请输入" style="width: 300px"/>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" placeHolder="请输入" style="width: 300px"/>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" placeHolder="请输入" style="width: 300px"/>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="editForm.password" placeHolder="请输入" style="width: 300px"/>
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
import {getAdministrators, insertAdministrator, updateAdministrator, deleteAdministrator} from '@/api/admin'

export default {
  name: 'Administrator',
  data() {
    return {
      dataForm: {
        account: "",
        username: "",
        email: "",
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
        account: null,
        username: null,
        password: null,
        email: null
      },
      editVisible: false,
      editForm: {
        id: null,
        account: null,
        username: null,
        password: null,
        email: null
      },
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
      this.dataForm.account = "";
      this.dataForm.username = "";
      this.dataForm.email = "";
    },
    async pageData() {
      this.tableLoading = true
      getAdministrators(this.dataForm)
          .then((response) => {
            if (response.code === CONSTANTS.SUCCESS_CODE) {
              this.dataTable = response.data.dtpAdministrators
            }
            this.tableLoading = false
          })
          .catch((err) => {
            this.tableLoading = false
          })
    },
    showInsert() {
      this.insertForm.account = null
      this.insertForm.username = null
      this.insertForm.email = null
      this.insertForm.password = null
      this.insertVisible = true;
    },
    insert() {
      insertAdministrator(this.insertForm)
          .then((response) => {
            if (response.code === CONSTANTS.SUCCESS_CODE) {
              this.search();
              this.insertVisible = false
              this.insertForm.account = null
              this.insertForm.username = null
              this.insertForm.email = null
              this.insertForm.password = null
            }
          })
          .catch((err) => {
          })
    },
    showEdit(row) {
      this.editForm.id = row.id
      this.editForm.account = row.account
      this.editForm.username = row.username
      this.editForm.email = row.email
      this.editForm.password = row.password
      this.editVisible = true
    },
    edit() {
      updateAdministrator(this.editForm)
          .then((response) => {
            if (response.code === CONSTANTS.SUCCESS_CODE) {
              this.search();
              this.editVisible = false
              this.editForm.id = null
              this.editForm.account = null
              this.editForm.username = null
              this.editForm.email = null
              this.editForm.password = null
            }
          })
          .catch((err) => {
          })
    },
    remove(row) {
      deleteAdministrator({id: row.id})
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