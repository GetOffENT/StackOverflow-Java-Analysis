<template>
  <div>
    <h1>API Overview</h1>
    <nav>
      <ul>
        <li>
          <router-link to="/restful/index/toptopicfrequency">Top Topic Frequency</router-link>
          <span> - query the top N topics sorted by frequency</span> <!-- 添加解释 -->
        </li>
        <li>
          <router-link to="/restful/index/specifictopicfrequency">Specific Topic Frequency</router-link>
          <span> - query for the frequency of a specific topic </span> <!-- 添加解释 -->
        </li>
        <li>
          <router-link to="/restful/index/topbugfrequency">Top Bug Frequency</router-link>
          <span> - query the top N errors or exceptions sorted by frequency</span> <!-- 添加解释 -->
        </li>
        <li>
          <router-link to="/restful/index/specificbugfrequency">Specific Bug Frequency</router-link>
          <span> - query for the frequency of a specific bug</span> <!-- 添加解释 -->
        </li>
      </ul>
    </nav>
    <router-view /> <!-- 嵌套路由的视图 -->
  </div>
</template>

<script>
export default {
  name: 'RestfulIndex'
}
</script>

<style>
/* 添加样式 */
ul {
  list-style: none;
  padding: 0;
}

li {
  margin: 10px 0;
}

a {
  text-decoration: none;
  color: blue;
}

a:hover {
  color: darkblue;
}

/* 解释文字的样式 */
span {
  font-size: 14px;
  color: gray;
  margin-left: 10px;
}
</style>

