<template>
  <el-tree
    :data="menus"
    :props="defaultProps"

    show-checkbox
    node-key="id"
    :expand-on-click-node="false"
    @node-click="handleNodeClick">
     <span class="custom-tree-node" slot-scope="{ node, data }">
        <span>{{ data.name }}</span>
        <span>
          <el-button
            v-if="node.level <= 2"
            type="text"
            size="mini"
            @click="() => append(data)">
            Append
          </el-button>
          <el-button
            v-if="data.children.length === 0"
            type="text"
            size="mini"
            @click="() => remove(node, data)">
            Delete
          </el-button>
        </span>
      </span>
  </el-tree>
</template>

<script>
  export default {
    name: 'category',
    data () {
      return {
        menus: [],
        defaultProps: {
          children: 'children',
          label: 'name'
        }
      };
    },
    methods: {
      //新增
      append(data) {
       console.info("append == data ")
       console.info(data)

      },

      //删除
      remove(node, data) {
        this.$confirm(`确定删除分类${data.name}么?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/sys/config/delete'),
            method: 'post',
            data: this.$http.adornData(ids, false)
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.getDataList()
                }
              })
            } else {
              this.$message.error(data.msg)
            }
          })
        }).catch(() => {})
      },



      handleNodeClick(data) {
        console.log(data);
      },
      getMenus() {
        this.$http({
          url: this.$http.adornUrl('/product/category/list/tree'),
          method: 'get'
        }).then(({data}) => {
          console.info(data.code)
          if (data && data.code === 0) {
            console.info(data.code)
            console.info(data)
            this.menus = data.entities;
            console.info(data.entities)
            console.info(this.menus)
          } else {
            this.menus = [];
          }
        })
      }
    }
  ,
  created()
  {
    this.getMenus();
  }
  }
</script>

<style scoped>

</style>
