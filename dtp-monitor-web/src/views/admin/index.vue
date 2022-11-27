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
            label="ID"
            width="180">
        </el-table-column>
        <el-table-column
            prop="account"
            label="账号"
            width="180">
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
import {getAdministrators} from '@/api/admin'

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
  }
}
</script>